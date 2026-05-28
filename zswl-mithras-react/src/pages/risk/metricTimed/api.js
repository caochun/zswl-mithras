import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/risk/metric/timed/list', params, {}),
  allSelect: (params) => http.get('/risk/metric/select', params, {}),
  getAllIndustry: (params) => http.get('/select/industry/all', { params }),
}
