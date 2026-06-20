import { TableStore, App, ModalStore, Modal } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import Api from '@/api/cpm/payment/paymentApplicationApi'
import { timeFormat } from '@/utils'
import { debounce as _debounce } from 'lodash'
import moment from 'moment'

const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      const currentData = {
        ...searchData,
        applyPaymentAmountFrom:
          searchData.applyPaymentAmount && searchData.applyPaymentAmount[0]
            ? searchData.applyPaymentAmount[0] * 10000
            : undefined,
        applyPaymentAmountTo:
          searchData.applyPaymentAmount && searchData.applyPaymentAmount[1]
            ? searchData.applyPaymentAmount[1] * 10000
            : undefined,
        applyPaymentDateFrom: searchData.applyPaymentDate
          ? timeFormat(searchData.applyPaymentDate[0])
          : undefined,
        applyPaymentDateTo: searchData.applyPaymentDate
          ? timeFormat(searchData.applyPaymentDate[1])
          : undefined,
        paidInDateFrom: searchData.paidInDate ? timeFormat(searchData.paidInDate[0]) : undefined,
        paidInDateTo: searchData.paidInDate ? timeFormat(searchData.paidInDate[1]) : undefined,
      }
      return Api.getWriteOffList(currentData)
    },
  })

  options = getData().optionsType
  getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (this.options && this.options[key]) {
      this.options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }

  toDetail = (id, bizType) => {
    if (id) {
      history.push(`/customer/maintain/detail/${id}?bizType=${bizType}`)
    }
  }

  fundConfirmModal = new ModalStore({
    onFinish: (values) => {
      const data = {
        ...values,
        assessDate: timeFormat(values.assessDate),
      }
      this.fundConfirmModal.close()
    },
  })

  collectionDayModal = new ModalStore({
    onOpen: async (record) => {
      if (record) {
        const defaultCollectionDay = await Api.getCollectionDay({
          id: record.paymentId,
        })
        return {
          defaultCollectionDay,
        }
      }
    },
  })
  // 修改付款对应收款日
  updateCollectionDay = async () => {
    const { rows } = this.table.getSelected()
    const values = await this.collectionDayModal.submit()
    await Api.updateCollectionDay({
      paymentId: rows[0].paymentId,
      defaultCollectionDay: values.defaultCollectionDay,
    })
    message.success('操作成功！')
    this.collectionDayModal.close()
  }
  // 结束投放
  paymentFinish = async () => {
    const { rows } = this.table.getSelected()
    const values = await this.collectionDayModal.submit()

    await Api.getPaymentWriteoffFinish({
      paymentId: rows[0].paymentId,
    })

    // 系统后台保存收款日
    await Api.updateCollectionDay({
      paymentId: rows[0].paymentId,
      defaultCollectionDay: values.defaultCollectionDay,
    })

    message.success('操作成功！')
    this.collectionDayModal.close()
    this.table.search()
  }
  finish = () => {
    const { rows } = this.table.getSelected()
    if (rows.length === 0) {
      message.info('请选中操作')
      return
    }
    this.collectionDayModal.open({
      paymentId: rows[0].paymentId,
    })
  }
}
export default new Store()
