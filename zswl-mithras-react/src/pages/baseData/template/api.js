import { http } from '@zswl/admin'

export default {
  postFileTemplateUpdate: (params) => http.post('/file/template/update', params),
  getFileDownload: (params) =>
    http.get('/file/download', {
      params,
      type: 'download',
      headers: {
        functionCode: 'filedownload',
      },
    }),
}
