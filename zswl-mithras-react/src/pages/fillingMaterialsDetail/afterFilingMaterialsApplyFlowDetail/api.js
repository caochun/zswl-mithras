import { http } from '@zswl/admin'

export default {
  getOperationsDirDict: (id) => http.post('/after/filingMaterial/getOperationsDirDict', { id }),
  batchDownload: (params) =>
    http.post(
      '/after/filingMaterial/batchDownload',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),
  remove: (params) =>
    http.post('/after/filingMaterial/file/remove', params, { transformResult: (res) => res.data }),
}
