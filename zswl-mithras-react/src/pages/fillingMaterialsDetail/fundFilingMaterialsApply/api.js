import { http } from '@zswl/admin'

export default {
  getOperationsDirDict: (id) => http.post('/fund/filingMaterial/getOperationsDirDict', { id }),
  download: (params) =>
    http.get('/fund/filingMaterial/download', { params, type: 'download', timeout: 0 }),
  batchDownload: (params) =>
    http.post(
      '/fund/filingMaterial/batchDownload',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),
}
