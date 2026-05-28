import { http } from '@zswl/admin'

export default {
  // 统计
  postMonitorStatistics: (params) => http.post('/clientMonitor/statistic', params),
  //预警列表
  postWarnlist: (params) => http.post('/clientMonitor/list', params),

  // rigth
  postQuantityChange: (params) => http.post('/clientMonitor/risk/linechart', params),
  // right Pie
  postQuantityPie: (params) => http.post('/clientMonitor/risk/piechart', params),
  // 舆情
  postMonitorList: (params) => http.post('/risk/warn/monitor/opinion/list', params),
  selectAll: (params) => http.get('/select/all', params),

  // 客户监控舆情详情
  postclientMonitorOpinionDetail: (params) => http.post('/clientMonitor/detail/opinion', params),
  //客户监控预警详情
  postclientMonitorRiskDetail: (params) => http.post('/clientMonitor/detail/warn', params),

  // 客户监控预警详情
  postYjDetail: (params) => http.post('/clientMonitor/detail/warn', params),
}
