import { message } from 'antd'
import { FormStore, PageStore, TableStore } from '@zswl/components'
import paymentApprovalApi from '@/api/financial/paymentApprovalApi'
import { history, makeAutoObservable } from '@zswl/admin'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      return paymentApprovalApi.postBatchDetail(params)
    },
  })
  defaultSelectedRowKeys = []
  sumData = {}
  table = new TableStore({
    request: async (params) => {
      const { id, businessVersion } = this.page.getParams()
      const res = await paymentApprovalApi.postRepayBatchReceiptList({
        id,
        version: businessVersion,
      })

      setTimeout(() => {
        this.table.selectAll()
      }, 0)
      this.sumData = res.reduce((pre, cur) => {
        console.log('pre: ', pre, cur)
        pre.financingAmount = (pre?.financingAmount ?? 0) + cur.financingAmount
        pre.paidInterest = (pre?.paidInterest ?? 0) + cur.paidInterest
        pre.paidPrincipal = (pre?.paidPrincipal ?? 0) + cur.paidPrincipal
        pre.planedRepayAmount = (pre?.planedRepayAmount ?? 0) + cur.planedRepayAmount
        pre.planedRepayInterest = (pre?.planedRepayInterest ?? 0) + cur.planedRepayInterest
        pre.planedRepayPrincipal = (pre?.planedRepayPrincipal ?? 0) + cur.planedRepayPrincipal
        return pre
      }, {})
      return res
    },
  })
  form = new FormStore()
  download = async () => {
    const { keys } = this.table.getSelected()
    const batchId = this.page.getParams().id
    const params = {
      batchId,
      receiptIdList: keys,
    }
    await paymentApprovalApi.postRepayBatchReceiptDownload(params)
  }
  cancel = async () => {
    history.push(`/financial/payment`)
  }
  submit = async (remark: string, repayMonth: string|undefined) => {
    const { keys } = this.table.getSelected()
    const batchId = this.page.getParams().id
    let params = {
      batchId,
      receiptIdList: keys,
      remark,
    }
    if(repayMonth){
      params = Object.assign(params, {repayMonth})
    }

    await paymentApprovalApi.postRepayBatchSubmit(params)
    message.success('提交成功')
    history.push(`/financial/payment`)
  }
}
export default new Store()
