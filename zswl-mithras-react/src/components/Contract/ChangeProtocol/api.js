import { http } from '@zswl/admin'

export default {
  postDataList: (params) => http.post('/contract/flow/change/down', params),
  postDataUpload: (params) =>
    http.post('/contract/flow/change/upload', params, {
      type: 'upload',
      timeout: 0,
    }),
}
