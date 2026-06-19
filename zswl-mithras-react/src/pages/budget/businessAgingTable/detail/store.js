import { FormStore, ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import agingDetailApi from '@/api/budget/aging/agingDetailApi'
import businessAgingApi from '@/api/budget/aging/businessAgingApi'
import { message } from 'antd'
import moment from 'moment'
// import { downLoadExcel, downloadZip } from '@/components/Excel'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const res = await businessAgingApi.postInfoDetail(params)
      const res2 = await agingDetailApi.postBaseInfoCount(params)
      setTimeout(() => {
        this.form.setFieldsValue({
          ...res,
          ...res2,
        })
      }, 100)
      return res
    },
  })
  table = new TableStore({
    request: async (params) => {
      const { id } = this.page.getData()
      return await agingDetailApi.postItemList({ ...params, accountAgeId: id })
    },
  })
  form = new FormStore()
  addModal = new ModalStore({
    onOpen: async (record) => {
      const { deadline } = this.page.getData() ?? {}
      if (record) {
        return {
          ...record,
          businessDate: record.businessDate && moment(record.businessDate),
          agingDeadline: record.agingDeadline && moment(record.agingDeadline),
          planCollectionDate: record.planCollectionDate && moment(record.planCollectionDate),
        }
      }
      return {
        accountancyOrganizationNumber: '10000396',
        currency: 'CNY',
        accountNumber: 'LONG_TERM_ACCOUNT_RECEIVABLE',
        paymentContent: 'KX08',
        customerUnitName: 'fbzdy999999',
        agingDeadline: deadline && moment(deadline),
      }
    },
    onFinish: async (values) => {
      const {
        originalValueInitial,
        originalValueIncrease,
        originalValueReduce,
        originalValueFinal,
      } = values
      const isEqual =
        (+originalValueFinal).toFixed(2) ===
        (+originalValueInitial + +originalValueIncrease - +originalValueReduce).toFixed(2)

      if (!isEqual) {
        message.error('期末款项原值（余额）不等于 期初款项原值+本期增加额-本期减少额')
        return false
      }
      const { id: accountAgeId } = this.page.getData()
      if (this.isAddModal) {
        await agingDetailApi.postItemAdd({ ...values, accountAgeId })
      } else {
        await agingDetailApi.postItemModify({
          ...values,
          accountAgeId,
          id: this.addModal.getInitialValues().id,
        })
      }
      message.success('操作成功')
      this.addModal.close()
      this.table.search()
    },
  })
  isAddModal = true
  add = () => {
    this.isAddModal = true
    this.addModal.open()
  }

  edit = () => {
    const { keys, rows } = this.table.getSelected()
    if (keys.length === 1) {
      this.isAddModal = false
      this.addModal.open(rows[0])
    } else {
      message.info('请选中1条记录编辑')
    }
  }
  reload = async () => {
    const { id: accountAgeId } = this.page.getData()
    const { keys } = this.table.getSelected()
    await agingDetailApi.postItemRegeneration({ ids: keys, accountAgeId })
    message.success('重新生成成功')
    this.table.search()
  }
  push = async () => {
    const { id: accountAgeId } = this.page.getData()
    const { keys } = this.table.getSelected()
    await agingDetailApi.postItemSend({ ids: keys, accountAgeId })
    message.success('推送成功')
    this.table.search()
  }
  complete = async () => {
    const { id } = this.page.getData()
    await businessAgingApi.postInfoEffect({ id })
    message.success('更改状态成功')
    this.page.init()

    this.table.search()
  }
  delete = async () => {
    const { id: accountAgeId } = this.page.getData()
    const { keys } = this.table.getSelected()
    await agingDetailApi.postItemRemove({ ids: keys, accountAgeId })
    message.success('删除成功')
    this.table.search()
  }
}
export default new Store()
