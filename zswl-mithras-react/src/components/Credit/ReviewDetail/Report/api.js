import { http } from '@zswl/admin'

export default {
  postReportList: (params) => http.post('/group/credit/review/report/list', params),
  postReportRemove: (params) =>
    http.post('/group/credit/review/report/remove', params, {
      transformResult: (res) => res.data,
    }),
  postReportUpload: (params) =>
    http.post('/group/credit/review/report/upload', params, {
      type: 'upload',
      transformResult: (res) => res.data,
      timeout: 0,
    }),
  postReportDownload: (params) =>
    http('/group/credit/review/report/download', {
      params,
      type: 'download',
      timeout: 0,
    }),
}
