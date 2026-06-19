import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/process/prepare/detail/list', params),
  refresh: (params) => http.post('/process/prepare/refresh', params),
  update: (params) => http.post('/process/prepare/bankinfo/modify', params),
  downLoadList: (data) =>
    http.post('/process/prepare/detail/list/download', data, { type: 'download' }),
  downLoadFiles: (data) =>
    http.post('/process/prepare/detail/batch/download', data, { type: 'download' }),
  downLoadFile: (data) =>
    http.post('/process/prepare/detail/single/download', data, { type: 'download' }),
  previewFile: (data) =>
    http.post('/process/prepare/detail/single/preview', data, {
      transformResult: (res) => {
        return res?.data
      },
    }),
}
