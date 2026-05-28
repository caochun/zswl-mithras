import { http } from '@zswl/admin'

export default {
  // 业务工作台-项目视图-计划执行情况-按公司统计
  postDashboardPlanStatistics: (data) => http.post('/dashboard/plan/statistics', data),
  // 业务工作台-项目视图-计划执行情况-按部门统计
  postDashboardPlanStatisticsByDept: (data) => http.post('/dashboard/plan/statistics/bydept', data),
  // 业务工作台-项目视图-计划执行情况
  postDashboardPlanList: (data) => http.post('/dashboard/plan/list', data),
}
