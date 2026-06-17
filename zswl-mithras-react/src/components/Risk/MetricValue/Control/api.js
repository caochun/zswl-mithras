import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/risk/control/jzd/report/list', params, { mock: false }),
  modify: (params) => http.post('/risk/control/jzd/report/modify', params),
  remove: (params) => http.post('/risk/control/jzd/report/remove', params),
  add: (params) => http.post('/risk/control/jzd/report/add', params),
  report: (params) => http.post('/risk/control/jzd/report/submit', params, {}),
}
