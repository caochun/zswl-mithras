import bankFlowProcessingCenterApi from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'
import flowCenterApi from '@/api/budget/flowCenter/flowCenterApi'
import { makeAutoObservable } from '@zswl/admin'
import { App, ModalStore, PageStore, TableStore } from '@zswl/components'
import { message } from 'antd'

class Store {
  constructor({ getCount }) {
    makeAutoObservable(this)
    this.getCount = getCount
  }
  radioValue = 'receipt'
  radioChange = (e) => {
    this.radioValue = e.target.value
    this.table.reset()
  }
  dimension = 'fundamental'
  dimensionChange = (e) => {
    this.dimension = e.target.value
    this.table.reset()
  }
  page = new PageStore({
    request: (params) => {},
  })
  fundamentalsTable = new TableStore({
    request: async (params) => {
      return bankFlowProcessingCenterApi.postFinanceList(params)
    },
  })
  table = new TableStore({
    request: async (params) => {
      const isReceipt = this.radioValue === 'receipt'
      this.getCount()
      return isReceipt ? flowCenterApi.postCollectionList(params) : flowCenterApi.postPaymentList(params)
    },
  })

  offModal = new ModalStore({
    onOpen: async (record) => {
      const { paidInDate, paymentAmount, paidAmount, ...rest } = record

      return { ...rest }
    },
    onFinish: async (values) => {
      const {
        billCode,
        billAmount,
        billExpireDate,
        paymentMethod,
        paidAmount,
        paymentAmount,
        ...rest
      } = values

      if (values.paidInAmount > paymentAmount - paidAmount) {
        return message.error('核销金额不能大于应付款')
      }
      const billManagementAddREQ = {
        billCode,
        billAmount,
        billExpireDate,
        mainId: values.paymentId,
        billType: 'PAYMENT',
      }

      await flowCenterApi.postManualRecord({
        ...rest,
        paymentAmount,
        billManagementAddREQ,
        paymentMethod: App.matchOption('paymentMethod', paymentMethod)?.label,
      })
      this.offModal.close()
      message.success('核销成功')
      this.table.search()
    },
  })
  receiptOffModal = new ModalStore({
    onOpen: async (record) => {
      const { collectionDate, ...rest } = record
      return { ...rest }
    },
    onFinish: async (values) => {
      const {
        paidInPrincipal: principal,
        paidInInterest: interest,
        paidInPenaltyInterest: penaltyInterest,
        code: collectionCode,
        collectionType,
        collectionId,
        billCode,
        billAmount,
        billExpireDate,
        billBuyRateType,
        billBuyRate,
        actualAmountReceive,
        ...rest
      } = values
      const billManagementAddREQ = {
        billCode,
        billAmount,
        billExpireDate,
        billBuyRateType: billBuyRateType ? 1 : 0,
        billBuyRate,
        mainId: collectionId,
        billType: 'COLLECTION',
      }

      await Api.collectionManualRecord({
        ...rest,
        collectionId,
        principal,
        interest,
        penaltyInterest,
        collectionCode,
        billManagementAddREQ,
        collectionAmount: actualAmountReceive,
        collectionType: App.matchOption('paymentMethod', collectionType)?.label,
      })
      this.receiptOffModal.close()
      message.success('核销成功')
      this.table.search()
    },
  })
  detailTable = new TableStore({
    request: async (params) => {
      const { collectionId, paymentId, cashFlowItem } = this.detailModal.getInitialValues()
      const isReceipt = this.radioValue === 'receipt'
      const res = isReceipt
        ? await flowCenterApi.postCollectionSettleDetail({ ...params, collectionId })
        : await flowCenterApi.postSettleDetail({ cashFlowItem, paymentId })
      return res
    },
  })
  detailModal = new ModalStore({
    onOpen: async (record) => {
      return record
    },
  })
  handleOff = () => {
    const isReceipt = this.radioValue === 'receipt'
    const { rows } = this.table.getSelected()
    const record = rows[0]
    if ([1, '1'].includes(record.retreatLock)) {
      return message.error('该租金期次处于保证金退抵流程中，请等待结束后操作！')
    }
    if (!isReceipt && ['EARNEST_MONEY', 'RETENTION_MONEY'].includes(record.cashFlowItem)) {
      return message.error('请前往流水中心认领核销')
    }
    isReceipt ? this.receiptOffModal.open(record) : this.offModal.open(record)
  }
  openDetail = () => {
    const { rows } = this.table.getSelected()
    const record = rows[0]
    this.detailModal.open(record)
  }
  $checkLetter = new ModalStore({})
}
export default Store
