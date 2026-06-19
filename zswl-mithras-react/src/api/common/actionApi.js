import { http } from '@zswl/admin'

export default {
  postDelete: (url, data) => http.post(url, data),
  postUpload: (url, data) => http.post(url, data, { type: 'upload' }),
  downloadTemplate: (url, fileName) => http.get(url, { type: 'download', fileName }),
  exportFile: (data, functionCode) =>
    http.post('/file/export', data, {
      type: 'download',
      headers: {
        functionCode: functionCode || 'dashboardFileExport',
      },
    }),
}
