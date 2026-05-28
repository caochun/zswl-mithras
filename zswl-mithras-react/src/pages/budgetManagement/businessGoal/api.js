import { http } from '@zswl/admin'

export default {
  // 业绩目标列表
  postKpiPerformanceManageMainList: (params) =>
    http.post('/kpi/performance/manage/main/list', params),
  // 业绩详情
  postKpiPerformanceManageMainDetail: (params) =>
    http.post('/kpi/performance/manage/main/detail', params),
  // 业绩目标导出
  postKpiPerformanceManageExport: (params) =>
    http.post('/kpi/performance/manage/export', params, { type: 'download' }),
  // 业绩新增
  postKpiPerformanceManageAdd: (params) => http.post('/kpi/performance/manage/add', params),
  // 年度业绩目标
  postKpiPerformanceManageList: (params) => http.post('/kpi/performance/manage/list', params),

  // 业绩目标导入
  postKpiPerformanceManageImportant: (params) =>
    http.post('/kpi/performance/manage/import', params, { type: 'upload' }),
  // 业绩目标状态修改
  postKpiPerformanceManageModify: (params) => http.post('/kpi/performance/manage/modify', params),
}
