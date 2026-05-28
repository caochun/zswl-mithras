import { http } from '@zswl/admin'

export default {
  postReportGenerate: (params) =>
    http.post('/proj/establish/report/generateOnline', params, {
      transformResult: (res) => res.data,
    }),

  postReportUpload: (params, config) =>
    http.post('/proj/establish/report/upload', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
      },
      type: 'upload',
      ...config,
      transformResult: (res) => res.data,
      timeout: 0,
    }),
}
