import { http } from '@zswl/admin'

export default {
  postCashFlowList: (params) => http.post('/proj/pricing/cashflowplan/list', params),
  reviewCashflowCompare: (params) => http.post('/proj/pricing/cashflowplan/list/compare', params),
  reviewPricingCashflowCompare: (params) =>
    http.post('/proj/pricing/review/cashflowplan/list/compare', params),
  postMeetMinuteCompare: (params) =>
    http.post('/proj/pricing/cashflowplan/meet/minute/compare', params),
  postReportGenerate: (params) =>
    http.post('/proj/pricing/cashflowplan/generate', params, {
      transformResult: (res) => res.data,
    }),
  postCashFlowUpload: (params, config) =>
    http.post('/proj/pricing/cashflowplan/upload', params, {
      type: 'upload',
      transformResult: (res) => res.data,
      ...config,
      timeout: 0,
    }),
  postCashFlowUDownload: (params) =>
    http('/proj/pricing/cashflowplan/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  postCashFlowExport: (params) =>
    http.post('/proj/pricing/cashflowplan/cashflow/export', params, {
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  postRentExport: (params) =>
    http.post('/proj/pricing/cashflowplan/rent/export', params, {
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
}
