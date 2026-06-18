import { http } from '@zswl/admin'

export default {
  getReportList: (params) => http.post('/after/lease/report/list', params),
  getApprovalReportList: (params) => http.post('/after/lease/report/listApproval', params), // 审批流场景，获取列表
  downReport: (params) =>
    http('/after/lease/report/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  delReport: (params) => http.post('/after/lease/report/remove', params, {}),

  uploadReport: (params) =>
    http.post('/after/lease/report/upload', params, {
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
    }),
}
