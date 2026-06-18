import { http } from '@zswl/admin'

export default {
  postWarnlist: (params, functionCode = 'riskWarnMonitorWarnList') =>
    http.post('/risk/warn/monitor/warn/list', params, {
      headers: {
        functionCode,
      },
    }),
}
