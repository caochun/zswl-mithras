import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/financial/cloud/metric/value/list', params, {}),
  allSelect: (params) => http.get('/risk/metric/select', params, {}),
  report: (params) => http.post('/financial/cloud/metric/value/report', params),
  save: (params) => http.post('/financial/cloud/metric/value/modify', params),
  calc: (params) => http.post('/financial/cloud/metric/value/calc', params),
  pullDownList: (params) => http.post('/financial/cloud/metric/pulldownList', params),
  postMetricDetail: (params) => http.post('/financial/cloud/metric/detail', params),
}
