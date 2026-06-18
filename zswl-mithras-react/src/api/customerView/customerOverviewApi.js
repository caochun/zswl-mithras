import { http } from '@zswl/admin'
import customerOverviewApi from '@/api/dashboard/customerOverview'

export default {
  postDashboardClientOverviewStatistics: customerOverviewApi.postDashboardClientOverviewStatistics,
  postCustomerTrends: (data) =>
    http.post('/client/unified/view/customer/trends', data, { timeout: 0 }),
}
