import { http } from '@zswl/admin'

export default {
  postKpiPerformanceManageMainList: (params) =>
    http.post('/kpi/performance/manage/main/list', params),
  postKpiPerformanceManageMainDetail: (params) =>
    http.post('/kpi/performance/manage/main/detail', params),
  postKpiPerformanceManageExport: (params) =>
    http.post('/kpi/performance/manage/export', params, { type: 'download' }),
  postKpiPerformanceManageAdd: (params) => http.post('/kpi/performance/manage/add', params),
  postKpiPerformanceManageList: (params) => http.post('/kpi/performance/manage/list', params),
  postKpiPerformanceManageImportant: (params) =>
    http.post('/kpi/performance/manage/import', params, { type: 'upload' }),
  postKpiPerformanceManageModify: (params) => http.post('/kpi/performance/manage/modify', params),
}
