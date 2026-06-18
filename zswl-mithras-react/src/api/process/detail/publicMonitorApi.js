import { http } from '@zswl/admin'

export default {
  postMonitorDetail: (params) => http.post('/risk/control/opinion/monitor/detail', params),
  postMonitorFlowHandle: (params) =>
    http.post('/risk/control/opinion/monitor/advisement', params),
}
