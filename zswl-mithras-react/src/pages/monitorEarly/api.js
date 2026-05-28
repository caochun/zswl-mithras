import { http } from '@zswl/admin'

export default {
  // 统计
  postMonitorStatistics: (params) => http.post('/risk/warn/monitor/statistics', params),
  //预警列表
  postWarnlist: (params, functionCode = 'riskWarnMonitorWarnList') =>
    http.post('/risk/warn/monitor/warn/list', params, {
      headers: {
        functionCode,
      },
    }),

  // rigth
  postQuantityChange: (params) => http.post('/risk/warn/monitor/quantity/change', params),
  // right Pie
  postQuantityPie: (params) => http.post('/risk/warn/monitor/type/change', params),
  // 舆情
  postMonitorList: (params) => http.post('/risk/warn/monitor/opinion/list', params),
  selectAll: (params) => http.get('/select/all', params),
}
