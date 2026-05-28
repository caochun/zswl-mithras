import { http } from '@zswl/admin'

export default {
  getOperationsDirDict: (id) => http.post('/other/filingMaterial/getOperationsDirDict', { id }),
  getMaterialsDesc: (id) => http.post('/other/filingMaterial/getMaterialsDesc', { id }),
  remove: (params) =>
    http.post('/other/filingMaterial/file/remove', params, { transformResult: (res) => res.data }),
  submit: (params) => http.post('/other/filingMaterial/commit', params),
  cancel: (params) => http.get('/other/filingMaterial/cancel', { params }),
  batchDownload: (params) =>
    http.post(
      '/other/filingMaterial/batchDownload',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),
}
