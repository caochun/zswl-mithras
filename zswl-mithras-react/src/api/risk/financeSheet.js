import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/risk/metric/factor/list', params, {}),
  fileList: (params) => http.post('/risk/metric/factor/file/list', params, {}),
  remove: (params) => http.post('/risk/metric/factor/file/remove', params, {}),
  refresh: (params) => http.post('/risk/metric/factor/refresh', params, { timeout: 0 }),
  import: (params) =>
    http.post('/risk/metric/factor/import', params, {
      type: 'upload',
      timeout: 0,
    }),
  allSelect: (params) => http.get('/risk/metric/select', params, {}),
  factorList: (params) => http.post('/risk/metric/factor/detail/pagelist', params, {}),
}
