/* prettier-ignore-start */
import * as Types from './interface/weeklyReportDetailApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 预算管理-投放计划（月度）-项目周报-明细-列表
  postReportPageList: (data: Types.ReportPageListRequest): Promise<Types.ReportPageListResponse> =>
    http.post('/budget/plan/weekly/report/pageList', data, { mock }),

  // 预算管理-投放计划（月度）-项目周报-明细-列表-合同信息
  postPageListContract: (
    data: Types.PageListContractRequest
  ): Promise<Types.PageListContractResponse> =>
    http.post('/budget/plan/weekly/detail/pageList/contract', data, { mock }),

  // 预算管理-投放计划（月度）-项目周报-明细-删除
  postDetailBatchDelete: (
    data: Types.DetailBatchDeleteRequest
  ): Promise<Types.DetailBatchDeleteResponse> =>
    http.post('/budget/plan/pay/weekly/report/detail/batchDelete', data, { mock }),

  // 预算管理-投放计划（月度）-项目周报-明细-新增
  postDetailAdd: (data: Types.DetailAddRequest): Promise<Types.DetailAddResponse> =>
    http.post('/budget/plan/weekly/report/detail/add', data, { mock }),

  // 预算管理-投放计划（月度）-项目周报-明细-统计
  postDetailStatistics: (
    data: Types.DetailStatisticsRequest
  ): Promise<Types.DetailStatisticsResponse> =>
    http.post('/budget/plan/weekly/report/detail/statistics', data, { mock }),

  // 预算管理-投放计划（月度）-项目周报-明细-编辑
  postDetailModify: (data: Types.DetailModifyRequest): Promise<Types.DetailModifyResponse> =>
    http.post('/budget/plan/weekly/report/detail/modify', data, { mock }),

  // 预算管理-投放计划（月度）-项目周报-明细-部门确认情况
  postDeptConfirmList: (
    data: Types.DeptConfirmListRequest
  ): Promise<Types.DeptConfirmListResponse> =>
    http.post('/budget/plan/weekly/detail/deptConfirm/list', data, { mock }),

  // 预算管理-投放计划（月度）-项目周报-详情列表
  postDetailList: (data: Types.DetailListRequest): Promise<Types.DetailListResponse> =>
    http.post('/budget/plan/pay/weekly/report/detail/list', data, { mock }),
}

/* prettier-ignore-end */
