import { http } from '@zswl/admin'

export default {
  postDashboardClientAfterleaseStatistics: (data) =>
    http.post('/dashboard/client/afterlease/statistics', data, {}),

  postDashboardClientAfterleaseCheckList: (data) =>
    http.post('/dashboard/client/afterlease/check/list', data, {}),
  postDashboardClientAfterleaseCheckListExport: (data) =>
    http.post('/dashboard/client/afterlease/check/list/export', data, { type: 'download' }),
}
