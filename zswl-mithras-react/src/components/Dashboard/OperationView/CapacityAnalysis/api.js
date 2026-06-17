import { http } from '@zswl/admin'

export default {
  //业务工作台-运营视角-产能分析-部门产能分析
  postDashboardOperationPayStatistics: (data) =>
    http.post('/dashboard/operation/capacity/statistics', data, {}),

  //业务工作台-运营视角-详情
  postDashboardOperationPayList: (data) => http.post('/dashboard/operation/capacity/list', data),
}
