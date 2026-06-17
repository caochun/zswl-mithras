import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore, PageStore } from '@zswl/components'
import { compareDetail, timeFormat } from '@/utils'
import { message, Modal } from 'antd'
import fundRepayAccountApi from '@/api/financial/fundRepayAccountApi'
import moment from 'moment'
import { debounce as _debounce } from 'lodash'
import Api from '@/api/financial/fundApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore()
  $table = new TableStore({
    pagination: false,
    request: async (params) => {
      const { businessVersion, receiptRepayId, isFormApproval, detail } = this.page.getParams()
      if (detail) return detail
      if (isFormApproval) {
        return fundRepayAccountApi.postAccountListCompare({
          ...params,
          businessVersion,
          receiptRepayId,
        })
      }
      return fundRepayAccountApi.postAccountList({ ...params, receiptRepayId })
    },
  })

  remove = (record) => {
    Modal.confirm({
      title: '是否删除？',
      onOk: async () => {
        await fundRepayAccountApi.postAccountRemove({
          id: record.id?.value || record.id,
        })
        message.success('删除成功')
        this.$table.search()
      },
    })
  }

  curItem = {}
  accountBank
  onOrgChange = (value) => {
    this.accountBank = value
  }
  $createModal = new ModalStore({
    onOpen: (record) => {
      if (record) {
        if (this.isFormApproval) {
          const res = compareDetail(record)
          const result = res.newDetail
          this.curItem = result
          return result
        }
        this.curItem = record
        this.accountBank = record.accountBank
        this.getProjList(record.accountBank)
        const accountOpeningDate = record.accountOpeningDate && moment(record.accountOpeningDate)
        return {
          ...record,
          accountOpeningDate,
        }
      }
    },
    onFinish: async (values) => {
      const { receiptRepayId } = this.page.getParams()
      const { id, accountBank, bankAccountId, accountType, accountNumber, accountOpeningDate } =
        values
      const func = id ? fundRepayAccountApi.postAccountModify : fundRepayAccountApi.postAccountAdd
      const params = {
        id,
        receiptRepayId,
        accountBank,
        bankAccountId,
        accountType,
        accountNumber,
        accountOpeningDate: accountOpeningDate && timeFormat(accountOpeningDate),
      }
      await func(params)

      message.success(id ? '修改成功' : '新增成功')
      this.$createModal.close()
      this.$table.search()
    },
  })
  projList = []

  getProjList = _debounce(async (bank) => {
    const res = await Api.postPayAccountBackInfoList({
      accountBank: bank,
    })
    this.projList = res
  }, 500)
}
export default Store
