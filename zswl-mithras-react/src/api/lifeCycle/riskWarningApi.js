import { http } from '@zswl/admin'

export default {
  postMonitorList: (params) => http.post('/risk/control/opinion/monitor/list', params),
}
