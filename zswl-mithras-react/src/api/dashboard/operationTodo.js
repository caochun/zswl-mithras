import { http } from '@zswl/admin'

export default {
  // 业务工作台-运营视图-待办统计
  postDashboardOperationToDoStatistics: (data) =>
    http.post('/dashboard/operation/todo/statistics', data, {}),
}
