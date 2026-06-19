import { http } from '@zswl/admin'

export default {
  postReportGenerate: (params) => http.post('/proj/pricing/report/generate', params, {}),
  postReportUpload: (params, config) =>
    http.post('/proj/pricing/report/upload', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
      },
      ...config,
      transformResult: (res) => res.data,
      timeout: 0,
      type: 'upload',
    }),
}
