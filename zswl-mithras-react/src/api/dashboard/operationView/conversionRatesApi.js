import { http } from '@zswl/admin'

export default {
  postDashboardOperationTimeTerm: (data) =>
    http.post('/dashboard/operation/conversion/term', data, {}),
  postDashboardOperationTimeStatistics: (data) =>
    http.post('/dashboard/operation/conversion/statistics', data),
  postDashboardOperationTimeList: (data) =>
    http.post('/dashboard/operation/conversion/list', data, {}),
}
