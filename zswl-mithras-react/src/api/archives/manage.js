import { http } from '@zswl/admin'

export default {
  getList: (data) => http.post('/archives/list', data),
  detail: (data) => http.post('/archives/info', data),
  addFile: (data) => http.post('/archives/add', data),
  download: (data) => http.post('/archives/download/effect', data),
  remind: (data) => http.post('/archives/remind', data),
  search: (data) => http.post('/archives/establish/query', data),
  fileSearch: (data) => http.post('/archives/search', data),
  effect: (data) => http.post('/archives/effect', data),
  postUpload: (params) =>
    http.post('/file/upload', params, {
      transformResult: (res) => res.data,
      type: 'upload',
      timeout: 0,
      headers: {
        functionCode: 'ARCHIVESFileUpload',
      },
    }),
  getFileDownload: (params) =>
    http.get('/file/download', {
      params,
      type: 'download',
      headers: {
        functionCode: 'ARCHIVESFileDownload',
      },
    }),
  downloadEffect: (data) => http.post('/archives/download/effect', data),
  uploadInfo: (data) => http.post('/archives/file/upload/info', data),
  postFileRemove: (params, functionCode) =>
    http.post('/file/remove', params, {
      headers: {
        functionCode,
      },
      transformResult: (res) => res.data,
    }),
}
