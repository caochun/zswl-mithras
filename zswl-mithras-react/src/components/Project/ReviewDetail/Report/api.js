import { http } from '@zswl/admin'

export default {
  postReportList: (params) => http.post('/proj/review/report/list', params),
  postReportGenerate: (params) => http.post('/proj/review/report/generate', params, {}),
  postReportRemove: (params) =>
    http.post('/proj/review/report/remove', params, {
      transformResult: (res) => res.data,
    }),
  postReviewFileBatchRemove: (params) =>
    http.post('/file/batch/remove', params, {
      headers: {
        functionCode: 'projReviewFileBatchRemove',
      },
      transformResult: (res) => res.data,
    }),
  postReportUpload: (params, config) =>
    http.post('/proj/review/report/upload', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
      },
      ...config,
      transformResult: (res) => res.data,
      timeout: 0,
      type: 'upload',
    }),
  postReportDownload: (params) =>
    http('/proj/review/report/download', {
      params,
      type: 'download',
      fileName: params.filename,
      transformResult: (res) => res.data,
      timeout: 0,
    }),
  postOnlyOfficeParams: (params) => http.post('/onlyoffice/docDetail', params),
}
