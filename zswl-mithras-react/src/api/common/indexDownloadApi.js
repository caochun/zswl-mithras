import { http } from '@zswl/admin'

export default {
  getFileDownload: (params, { functionCode }) =>
    http.post('/index/download', params, {
      type: 'download',
      timeout: 0,
      headers: {
        functionCode,
      },
    }),
}
