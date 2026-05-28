import { http } from '@zswl/admin'

export default {
  // 业务工作台-项目视图-投放情况-按公司统计
  postDashboardPayStatistics: (data) => http.post('/dashboard/pay/statistics', data),
  // 业务工作台-项目视图-投放情况-按部门统计
  postDashboardPayStatisticsByDept: (data) => http.post('/dashboard/pay/statistics/bydept', data),

  // 业务工作台-项目视图-投放情况
  postDashboardPayList: (data) => http.post('/dashboard/pay/list', data),
  // 业务工作台-项目视图-投放情况-导出
  postDashboardPayListExport: (data) =>
    http.post('/dashboard/pay/list/export', data, { type: 'download' }),
}
