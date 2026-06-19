import { http } from '@zswl/admin'

export default {
  postDashboardAfterLeaseCheckStatistics: (params) =>
    http.post('/dashboard/after/lease/check/statistics', params, {}),

  postDashboardAfterLeaseCheckList: (params) =>
    http.post('/dashboard/after/lease/check/list', params, {}),
}
