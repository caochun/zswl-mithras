import { http } from '@zswl/admin'

export default {
  getRefresh: (params) =>
    http.get('/management/report/refresh', {
      params,
    }),
  showRefreshBtn: (params) =>
    http.get('/management/report/showRefreshBtn', {
      params,
    }),
  getReportList: (params) =>
    http.get('/management/report/group/list', {
      params,
    }),
}
