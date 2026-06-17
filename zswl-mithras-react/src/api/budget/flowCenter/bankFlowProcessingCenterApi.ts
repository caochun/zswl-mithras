/* prettier-ignore-start */
import * as Types from './interface/bankFlowProcessingCenterApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 业务流水-资金端列表
  postFinanceList: (data: Types.FinanceListRequest): Promise<Types.FinanceListResponse> =>
    http.post('/finance/list', data, { mock }),

  // 删除
  postCenterDelete: (data: Types.CenterDeleteRequest): Promise<Types.CenterDeleteResponse> =>
    http.post('/bank/center/delete', data, { mock }),

  // 手动拉取资金流水
  postPullFlow: (data: Types.PullFlowRequest): Promise<Types.PullFlowResponse> =>
    http.post('/bank/center/manual/pull/flow', data, { mock }),

  // 批量核销
  postWriteOff: (data: Types.WriteOffRequest): Promise<Types.WriteOffResponse> =>
    http.post('/bank/center/batch/write/off', data, { mock }),

  // 改变正在核销的流水展示状态
  postChangeShowinlist: (
    data: Types.ChangeShowinlistRequest
  ): Promise<Types.ChangeShowinlistResponse> =>
    http.post('/bank/center/change/showinlist', data, { mock }),

  // 无需处理
  postProcessingRequire: (
    data: Types.ProcessingRequireRequest
  ): Promise<Types.ProcessingRequireResponse> =>
    http.post('/bank/center/no/processing/require', data, { mock }),

  // 根据借据id和现金流项目查期项
  postPhaseList: (data: Types.PhaseListRequest): Promise<Types.PhaseListResponse> =>
    http.post('/bank/center/project/phase/list', data, { mock }),

  // 根据合同ID和现金流项目查询现金流编号
  postCodeList: (data: Types.CodeListRequest): Promise<Types.CodeListResponse> =>
    http.post('/bank/center/project/code/list', data, { mock }),

  // 根据合同ID查询借据编号
  postReceiptList: (data: Types.ReceiptListRequest): Promise<Types.ReceiptListResponse> =>
    http.post('/bank/center/project/receipt/list', data, { mock }),

  // 根据现金流ID获取对应子列表
  postSubList: (data: Types.SubListRequest): Promise<Types.SubListResponse> =>
    http.post('/bank/center/project/sub/list', data, { mock }),

  // 根据计划收款ID和期项和现金流项目查询金额
  postAmountDetail: (data: Types.AmountDetailRequest): Promise<Types.AmountDetailResponse> =>
    http.post('/bank/center/project/amount/detail', data, { mock }),

  // 银行流水中心-各个tab列表
  postCenterList: (data: Types.CenterListRequest): Promise<Types.CenterListResponse> =>
    http.post('/bank/center/list', data, { mock }),
  // 确认收入
  postConfirmIncome: (data: any): Promise<any> =>
    http.post('/bank/center/confirm/income', data, { mock }),

  // 轧差退款
  postNettingRefund: (data: any): Promise<any> =>
    http.post('/bank/center/netting/refund', data, { mock }),
  // 根据合同ID和现金流项目查询现金流编号
  postBankCenterProjectCodeList: (data) =>
    http.post('/bank/center/project/code/list', data, { mock }),
  // 获取合同列表
  postCollectionFlowCenterBusinessPaymentManualCashFlowList: (data) =>
    http.post('/collection/flow/center/business/payment/manual/cashFlowList', data, { mock }),
  // 获取借据列表
  postFinanceRepaySplitWriteoffList: (data) =>
    http.post('/bank/center/finance/repay/split/writeoff/list', data, { mock }),
  // 银行流水还原至处理中心
  postBankCenterRestore: (data) => http.post('/bank/center/restore', data, { mock }),
}

/* prettier-ignore-end */
