import { http } from '@zswl/admin'

export default {
  postList: (params) => http.post('/financing/property/list', params),
  postListExport: (params) =>
    http.post('/financing/property/list/download', params, {
      type: 'download',
      timeout: 0,
    }),
}
