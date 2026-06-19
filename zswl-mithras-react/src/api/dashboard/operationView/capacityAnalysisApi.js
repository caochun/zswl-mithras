import { http } from '@zswl/admin'

export default {
  postDashboardOperationPayStatistics: (data) =>
    http.post('/dashboard/operation/capacity/statistics', data, {}),
  postDashboardOperationPayList: (data) => http.post('/dashboard/operation/capacity/list', data),
}
