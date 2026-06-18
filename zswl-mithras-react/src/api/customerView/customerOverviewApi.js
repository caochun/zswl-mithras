import { http } from '@zswl/admin'

export default {
  postDashboardClientOverviewStatistics: (
    data,
    functionCode = 'dashboardclientoverviewstatistics'
  ) =>
    http.post('/dashboard/client/overview/statistics', data, {
      headers: { functionCode },
      timeout: 0,
    }),
  postCustomerTrends: (data) =>
    http.post('/client/unified/view/customer/trends', data, { timeout: 0 }),
}
