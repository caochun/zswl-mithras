import { http } from '@zswl/admin'

export default {
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),
  uploadApproval: (params) => http.post('/proj/review/report/uploadApproval', params),
  // getApprovalList: (params) => http.post('/proj/review/report/listApproval', params),

  // 获取文件列表
  getApprovalList: (params) => http.post('/flow/file/listApproval', params),
  uploadApprovalList: (params) => http.post('/flow/file/uploadApproval', params),
  removeApprovalList: (params) => http.post('/flow/file/removeApproval', params),
  downloadFlowFile: (params, functionCode) =>
    http.get('/file/download', {
      params,
      type: 'download',
      headers: {
        functionCode,
      },
    }),

  // deleteReport: (params) => http.post('/proj/review/report/remove', params),
  deleteReport: (params) => http.post('/proj/review/report/removeApproval', params),

  //材料删除
  deletePaymentReport: (params) => http.post('/materials/payment/remove', params),

  // 租后项目调整
  uploadAdjustApproval: (params) => http.post('/after/lease/report/uploadApproval', params),
  getAdjustApprovalList: (params) => http.post('/after/lease/report/listApproval', params),

  //付款管理上传
  paymentUploadApproval: (params) => http.post('/materials/payment/uploadApproval', params),
  //付款管理列表
  getPaymentApprovalList: (params) => http.post('/materials/payment/listApproval', params),

  postReportDownload: (params) =>
    http('/proj/review/report/download', {
      recordId: params.id,
      type: 'download',
      fileName: params.fileName,
      transformResult: (res) => res.data,
      timeout: 0,
    }),

  generateEarningsRate: (params) => http.post('/proj/review/report/generate/earningsRate', params),
}
