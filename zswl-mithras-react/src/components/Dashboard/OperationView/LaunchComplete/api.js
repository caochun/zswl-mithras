import { http } from '@zswl/admin'

export default {
  // 业务工作台-运营视角-投放情况
  postDashboardOperationPayStatistics: (data) =>
    http.post('/dashboard/operation/pay/statistics', data, {}),

  //业务工作台-运营视角-投放情况-详情
  postDashboardOperationPayList: (data) => http.post('/dashboard/operation/pay/list', data),
}
