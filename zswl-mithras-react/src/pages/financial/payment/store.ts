import { Modal, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import paymentApprovalApi from '@/api/financial/paymentApprovalApi'
import fundReceiptRepayBaseInfoApi from '@/api/financial/fundReceiptRepayBaseInfoApi'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  sumData = {}
  table = new TableStore({
    pagination: {
      pageSizeOptions: [10, 20, 50, 100, 200, 500, 1000],
    },
    request: async (params) => {
      // return Api.getDetail({ id: params?.id })
      let { list, ...sumData } = await fundReceiptRepayBaseInfoApi.postInfoList({
        ...params,
        pageSize: 9999,
      })
      this.sumData = sumData
      if (
        params.filterAmount !== 0 ||
        (params?.filterAmount === 1 && list && list.list.length > 0)
      ) {
        return list.list
          .flat(Infinity)
          .filter((item) => item.monthRepayAmount && item.monthRepayAmount > 0)
      } else {
        return list.list.flat(Infinity)
      }
    },
  })
  batchApproval = async (batchType: 'BATCH' | 'AUTO') => {
    const { keys } = this.table.getSelected()
    const { repayMonth } = this.table.getSearchStore().getParams()

    await paymentApprovalApi
      .postRepayCreateBatch({
        batchType,
        receiptIdList: keys,
        repayMonth: repayMonth && moment(repayMonth).format('yyyy-MM-DD'),
      })
      .then((res) => {
        let _url = `/financial/payment/batchApproval/${res}`
        if (repayMonth) {
          _url += `?repayMonth=${moment(repayMonth).format('yyyy-MM-DD')}`
        }
        history.push(_url)
      })
  }
  delete = () => {
    const { keys } = this.table.getSelected()
    Modal.confirm({
      title: '提示',
      content: '确定关闭还款吗？',
      onOk: () => {
        fundReceiptRepayBaseInfoApi.postRepayBatchClose({ ids: keys }).then(() => {
          this.table.search()
        })
      },
    })
  }
}
export default new Store()
