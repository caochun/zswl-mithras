import { http } from '@zswl/admin'

export default {
  //业务工作台-项目视图-计划执行情况-运营审批时效统计
  postDashboardOperationApprovalStatistics: (data) =>
    http.post('/dashboard/operation/approval/statistics', data),

  //业务工作台-合同审批时效及退回情况-运营审批时效
  postDashboardOperationApprovalList: (data) =>
    http.post('/dashboard/operation/approval/list', data),

  //业务工作台-合同审批时效及退回情况-合同退回列表
  postDashboardOperationContractReturnList: (data) =>
    http.post('/dashboard/operation/contract/return/list', data),

  //业务工作台-项目视图-计划执行情况-运营审批时效统计
  postDashboardOperationContractReturnStatistics: (data) =>
    http.post('/dashboard/operation/contract/return/statistics', data),
}
