import { http } from '@zswl/admin'

export default {
  postDashboardOperationPayStatistics: (data) =>
    http.post('/dashboard/operation/pay/statistics', data, {}),
  postDashboardOperationPayList: (data) => http.post('/dashboard/operation/pay/list', data),
}
