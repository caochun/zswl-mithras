import { http } from '@zswl/admin'

export default {
  // 本年租赁业务投放收益率情况表
  postDashboardPayReceiptRate: (data) => http.post('/dashboard/pay/receipt/rate', data),

  // 业务工作台-项目视图-投放情况
  postDashboardPayList: (data) => http.post('/dashboard/pay/list', data),
  // 业务工作台-项目视图-投放情况-导出
  postDashboardPayListExport: (data) =>
    http.post('/dashboard/pay/list/export', data, { type: 'download' }),
}
