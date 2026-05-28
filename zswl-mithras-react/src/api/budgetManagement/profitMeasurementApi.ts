/* prettier-ignore-start */
import * as Types from './interface/profitMeasurementApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 预算管理-预算计划-利润预算-信息
  postProfitInfo: (data: Types.ProfitInfoRequest): Promise<Types.ProfitInfoResponse> =>
    http.post('/budget/plan/profit/info', data, { mock }),

  // 预算管理-预算计划-利润预算-列表
  postProfitPageList: (data: Types.ProfitPageListRequest): Promise<Types.ProfitPageListResponse> =>
    http.post('/budget/plan/profit/pageList', data, { mock }),

  // 预算管理-预算计划-利润预算-创建预算
  postProfitCreate: (data: Types.ProfitCreateRequest): Promise<Types.ProfitCreateResponse> =>
    http.post('/budget/plan/profit/create', data, { mock }),

  // 预算管理-预算计划-利润预算-删除
  postProfitRemove: (data: Types.ProfitRemoveRequest): Promise<Types.ProfitRemoveResponse> =>
    http.post('/budget/plan/profit/remove', data, { mock }),

  // 预算管理-预算计划-利润预算-发送待办（启动投放计划收集流程）
  postDetailNotifyCreatePlanPay: (
    data: Types.DetailNotifyCreatePlanPayRequest,
  ): Promise<Types.DetailNotifyCreatePlanPayResponse> =>
    http.post('/budget/plan/profit/detail/notifyCreatePlanPay', data, { mock }),

  // 预算管理-预算计划-利润预算-投放进度
  postProfitProcess: (data: Types.ProfitProcessRequest): Promise<Types.ProfitProcessResponse> =>
    http.post('/budget/plan/profit/process', data, { mock }),

  // 预算管理-预算计划-利润预算-汇总
  postProfitSummary: (data: Types.ProfitSummaryRequest): Promise<Types.ProfitSummaryResponse> =>
    http.post('/budget/plan/profit/summary', data, { mock }),

  // 预算管理-预算计划-利润预算-汇总（其他类型）
  postProfitSummaryOther: (
    data: Types.ProfitSummaryOtherRequest,
  ): Promise<Types.ProfitSummaryOtherResponse> =>
    http.post('/budget/plan/profit/summaryOther', data, { mock }),

  // 预算管理-预算计划-利润预算-预算明细-存量
  postDetailHistory: (data: Types.DetailHistoryRequest): Promise<Types.DetailHistoryResponse> =>
    http.post('/budget/plan/profit/detail/history', data, { mock }),

  // 预算管理-预算计划-利润预算-预算明细-新增
  postDetailFeature: (data: Types.DetailFeatureRequest): Promise<Types.DetailFeatureResponse> =>
    http.post('/budget/plan/profit/detail/feature', data, { mock }),

  // 预算管理-预算计划-利润预算-预算明细-部门详情
  postDetailDept: (data: Types.DetailDeptRequest): Promise<Types.DetailDeptResponse> =>
    http.post('/budget/plan/profit/detail/dept', data, { mock }),

  // 预算管理-预算计划-利润预算-预算明细-部门详情-编辑
  postDeptModify: (data: Types.DeptModifyRequest): Promise<Types.DeptModifyResponse> =>
    http.post('/budget/plan/profit/detail/dept/modify', data, { mock }),

  // 预算管理-预算计划-利润预算-预算确认
  postProfitConfirm: (data: Types.ProfitConfirmRequest): Promise<Types.ProfitConfirmResponse> =>
    http.post('/budget/plan/profit/confirm', data, { mock }),
}

/* prettier-ignore-end */
