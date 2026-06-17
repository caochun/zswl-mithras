import { http } from '@zswl/admin'

export default {
  // 客户一览统计
  postDashboardClientOverviewStatistics: (
    data,
    functionCode = 'dashboardclientoverviewstatistics'
  ) =>
    http.post('/dashboard/client/overview/statistics', data, {
      headers: { functionCode },
      timeout: 0,
    }),

  // 所有客户明细
  postDashboardClientOverviewAllList: (data) =>
    http.post('/dashboard/client/overview/all/list', data),

  // 存续客户明细
  postDashboardClientOverviewSurvivalList: (data) =>
    http.post('/dashboard/client/overview/survival/list', data),

  // 3个月内结清客户明细
  postDashboardClientOverviewSettleinthreemonthList: (data) =>
    http.post('/dashboard/client/overview/settleinthreemonth/list', data),

  // 逾期客户明细
  postDashboardClientOverviewOverdueList: (data) =>
    http.post('/dashboard/client/overview/overdue/list', data),

  // 已结清客户明细
  postDashboardClientOverviewSettledList: (data) =>
    http.post('/dashboard/client/overview/settled/list', data),
}
