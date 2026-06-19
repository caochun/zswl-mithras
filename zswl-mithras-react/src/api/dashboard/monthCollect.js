import { http } from '@zswl/admin'

export default {
  // 业务工作台-运营部-时效统计-时间周期
  postDashboardMonthcollectStatistics: (data) =>
    http.post('/dashboard/monthcollect/statistics', data, {}),

  // 收入分成列表信息
  postIncomeSharingList: (params) => http.post('/incomeSharing/list', params),

  // 收入分成列表下载
  postIncomeSharingListDownload: (params) =>
    http.post('/incomeSharing/list/download', params, { type: 'download' }),
}
