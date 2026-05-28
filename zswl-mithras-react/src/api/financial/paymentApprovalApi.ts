/* prettier-ignore-start */
import * as Types from './interface/paymentApprovalApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 批量操作-付款列表下载
  postRepayBatchReceiptDownload: (
    data: Types.RepayBatchReceiptDownloadRequest
  ): Promise<Types.RepayBatchReceiptDownloadResponse> =>
    http.post('/fund/receipt/repay/batchReceiptDownload', data, { mock, type: 'download' }),

  // 创建批次
  postRepayCreateBatch: (
    data: Types.RepayCreateBatchRequest
  ): Promise<Types.RepayCreateBatchResponse> =>
    http.post('/fund/receipt/repay/createBatch', data, { mock }),

  // 单个提交审批
  postRepaySubmit: (data: Types.RepaySubmitRequest): Promise<Types.RepaySubmitResponse> =>
    http.post('/fund/receipt/repay/submit', data, { mock }),

  // 批量提交审批
  postRepayBatchSubmit: (
    data: Types.RepayBatchSubmitRequest
  ): Promise<Types.RepayBatchSubmitResponse> =>
    http.post('/fund/receipt/repay/batchSubmit', data, { mock }),

  // 批量操作-付款列表
  postRepayBatchReceiptList: (
    data: Types.RepayBatchReceiptListRequest
  ): Promise<Types.RepayBatchReceiptListResponse> =>
    http.post('/fund/receipt/repay/batchReceiptList', data, { mock }),

  // 资金收付款版本列表
  postVersionList: (data: Types.VersionListRequest): Promise<Types.VersionListResponse> =>
    http.post('/fund/receipt/repay/version/list', data, { mock }),
  // 批量版本详情页
  postBatchDetail: (data: Types.VersionListRequest): Promise<Types.VersionListResponse> =>
    http.post('/fund/receipt/repay/batchDetail', data, { mock }),

  // 资金收付款版本比较详情（与上一版本比较）
  postComparePreVersion: (
    data: Types.ComparePreVersionRequest
  ): Promise<Types.ComparePreVersionResponse> =>
    http.post('/fund/receipt/repay/compare/preVersion', data, { mock }),
  // 费用一览表列表
  postExpenseList: (data: any): Promise<any> =>
    http.post('/fund/receipt/repay/expense/list/compare', data, { mock }),
  // 保证金明细列表
  postDepositList: (data: any): Promise<any> =>
    http.post('/fund/receipt/repay/cash/deposit/list/compare', data, { mock }),
  // 本金利息一览表列表
  postFlowList: (data: any): Promise<any> =>
    http.post('/fund/receipt/repay/cash/flow/list/compare', data, { mock }),

  // 借款流入列表
  postBorrowingList: (data: any): Promise<any> =>
    http.post('/fund/receipt/repay/borrowing/list/compare', data, { mock }),
  // 质押明细接口
  postPledgeDetail: (data: any): Promise<any> =>
    http.post('/fund/receipt/repay/base/info/pledge/detail/compare', data, { mock }),

  // 资金管理-融资管理-对方收款账户列表
  postReceiptAccountList: (data: any): Promise<any> =>
    http.post('/fund/receipt/account/list/compare', data, { mock }),
  // 资金管理-融资管理-我方付款账户列表
  postRepayAccountList: (data: any): Promise<any> =>
    http.post('/fund/repay/account/list/compare', data, { mock }),
}

/* prettier-ignore-end */
