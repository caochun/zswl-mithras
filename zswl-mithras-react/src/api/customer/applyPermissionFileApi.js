import { http } from '@zswl/admin'

export default {
  fileUpload: (params) =>
    http.post('/client/file/upload', params, {
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
    }),
  fileList: (params) => http.post('/client/file/list', params),
  fileBatchRemove: (params) =>
    http.post('/client/file/batch/remove', params, {
      transformResult: (res) => res.data,
    }),
  fileDownload: (params) =>
    http.post('/client/file/download', params, { type: 'download', timeout: 0 }),
  fileBatchDownLoad: (params) =>
    http.post('/client/file/batch/download', params, { type: 'download', timeout: 0 }),
}
