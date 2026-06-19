import { http } from '@zswl/admin'

export default {
  postDashboardOperationTimeTerm: (data) => http.post('/dashboard/operation/time/term', data, {}),
  postDashboardOperationTimeStatistics: (data) =>
    http.post('/dashboard/operation/time/statistics', data),
  postDashboardOperationTimeList: (data) => http.post('/dashboard/operation/time/list', data, {}),
}
