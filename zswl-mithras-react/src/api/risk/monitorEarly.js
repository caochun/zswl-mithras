import { http } from '@zswl/admin'

export default {
  postMonitorStatistics: (params) => http.post('/risk/warn/monitor/statistics', params),
  postWarnlist: (params, functionCode = 'riskWarnMonitorWarnList') =>
    http.post('/risk/warn/monitor/warn/list', params, {
      headers: {
        functionCode,
      },
    }),
  postQuantityChange: (params) => http.post('/risk/warn/monitor/quantity/change', params),
  postQuantityPie: (params) => http.post('/risk/warn/monitor/type/change', params),
  postMonitorList: (params) => http.post('/risk/warn/monitor/opinion/list', params),
  selectAll: (params) => http.get('/select/all', params),
}
