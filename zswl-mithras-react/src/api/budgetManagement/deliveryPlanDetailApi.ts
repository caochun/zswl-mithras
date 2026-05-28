/* prettier-ignore-start */
import * as Types from './interface/deliveryPlanDetailApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 预算管理-预算计划-投放计划-明细-删除
  postDetailBatchDelete: (
    data: Types.DetailBatchDeleteRequest
  ): Promise<Types.DetailBatchDeleteResponse> =>
    http.post('/budget/plan/pay/detail/batchDelete', data, { mock }),

  // 预算管理-预算计划-投放计划-明细-部门确认情况
  postDeptConfirmList: (
    data: Types.DeptConfirmListRequest
  ): Promise<Types.DeptConfirmListResponse> =>
    http.post('/budget/plan/pay/detail/deptConfirm/list', data, { mock }),

  // 预算管理-预算计划-投放计划-月度-明细-保存
  postDetailSave: (data: Types.DetailSaveRequest): Promise<Types.DetailSaveResponse> =>
    http.post('/budget/plan/pay/month/detail/save', data, { mock }),

  // 预算管理-预算计划-投放计划-月度-明细-列表
  postDetailPageList: (data: Types.DetailPageListRequest): Promise<Types.DetailPageListResponse> =>
    http.post('/budget/plan/pay/month/detail/pageList', data, { mock }),

  // 预算管理-预算计划-投放计划-月度-明细-列表-合同信息
  postPageListContract: (
    data: Types.PageListContractRequest
  ): Promise<Types.PageListContractResponse> =>
    http.post('/budget/plan/pay/month/detail/pageList/contract', data, { mock }),

  // 预算管理-预算计划-投放计划-月度-明细-新增
  postDetailAdd: (data: Types.DetailAddRequest): Promise<Types.DetailAddResponse> =>
    http.post('/budget/plan/pay/month/detail/add', data, { mock }),

  // 预算管理-预算计划-投放计划-月度-明细-统计
  postDetailStatistics: (
    data: Types.DetailStatisticsRequest
  ): Promise<Types.DetailStatisticsResponse> =>
    http.post('/budget/plan/pay/month/detail/statistics', data, { mock }),

  // 预算管理-预算计划-投放计划-月度-明细-编辑
  postDetailModify: (data: Types.DetailModifyRequest): Promise<Types.DetailModifyResponse> =>
    http.post('/budget/plan/pay/month/detail/modify', data, { mock }),


  checkPlanPayAmount: (data: Types.DetailModifyRequest): Promise<Types.DetailModifyResponse> =>
    http.post('/budget/plan/pay/month/detail/checkPlanPayAmount', data, { mock }),

  // 预算管理-预算计划-投放计划-月度-明细-选择项目评审
  postProjreviewList: (data: Types.ProjreviewListRequest): Promise<Types.ProjreviewListResponse> =>
    http.post('/budget/plan/pay/month/detail/projreview/list', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-列表
  postNotMonthDetailPageList: (
    data: Types.DetailPageListRequest
  ): Promise<Types.DetailPageListResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/pageList', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-基本信息
  postDetailBaseInfo: (data: Types.DetailBaseInfoRequest): Promise<Types.DetailBaseInfoResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/baseInfo', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-复制
  postDetailCopy: (data: Types.DetailCopyRequest): Promise<Types.DetailCopyResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/copy', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-报价方案
  postDetailPrice: (data: Types.DetailPriceRequest): Promise<Types.DetailPriceResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/price', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-收入分摊情况
  postDetailIncomeSharing: (
    data: Types.DetailIncomeSharingRequest
  ): Promise<Types.DetailIncomeSharingResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/incomeSharing', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-期间费用
  postDetailExpense: (data: Types.DetailExpenseRequest): Promise<Types.DetailExpenseResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/expense', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-测算
  postDetailCalculate: (
    data: Types.DetailCalculateRequest
  ): Promise<Types.DetailCalculateResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/calculate', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-现金流计划
  postDetailCashFlow: (data: Types.DetailCashFlowRequest): Promise<Types.DetailCashFlowResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/cashFlow', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-统计
  postNotMonthDetailStatistics: (
    data: Types.DetailStatisticsRequest
  ): Promise<Types.DetailStatisticsResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/statistics', data, { mock }),

  // 预算管理-预算计划-投放计划-非月度-明细-资金成本
  postDetailFundCost: (data: Types.DetailFundCostRequest): Promise<Types.DetailFundCostResponse> =>
    http.post('/budget/plan/pay/notmonth/detail/fundCost', data, { mock }),
}

/* prettier-ignore-end */
