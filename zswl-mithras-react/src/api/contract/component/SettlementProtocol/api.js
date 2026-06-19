import { http } from '@zswl/admin'

export default {
  postDataUpload: (params) =>
    http.post('/contract/settle/extra/file/upload', params, {
      type: 'upload',
      timeout: 0,
    }),
}
