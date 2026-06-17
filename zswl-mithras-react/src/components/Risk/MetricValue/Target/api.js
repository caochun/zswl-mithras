import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/risk/metric/value/list', params, {}),
  allSelect: (params) => http.get('/risk/metric/select', params, {}),
  report: (params) => http.post('/risk/metric/value/report', params),
  getAllIndustry: (params) => http.get('/select/industry/all', { params }),
  save: (params) => http.post('/risk/metric/value/modify', params),
  calc: (params) => http.post('/risk/metric/value/calc', params),
}
