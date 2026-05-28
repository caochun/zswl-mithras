import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/risk/metric/factor/list', params, {}),
  import: (params) =>
    http.post('/risk/metric/factor/import', params, {
      type: 'upload',
      timeout: 0,
    }),
  allSelect: (params) => http.get('/risk/metric/select', params, {}),
}
