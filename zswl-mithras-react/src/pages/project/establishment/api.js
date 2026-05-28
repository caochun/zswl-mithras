import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/proj/establish/base/info/list', params),
  postProject: (params) =>
    http.post('/proj/establish/base/info/add', params, {
      transformResult: (res) => res.data,
    }),
  remove: (params) => http.post('/proj/establish/base/info/disable', params),
}
