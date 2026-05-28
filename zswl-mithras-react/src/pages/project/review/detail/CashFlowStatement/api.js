import { http } from '@zswl/admin'

export default {
  postCashFlowList: (params) => http.post('/proj/review/cashflowplan/list', params),
  reviewCashflowCompare: (params) => http.post('/proj/review/cashflowplan/list/compare', params),
  reviewPricingCashflowCompare: (params) =>
    http.post('/proj/review/pricing/cashflowplan/list/compare', params),
  postReportGenerate: (params) =>
    http.post('/proj/review/cashflowplan/generate', params, {
      transformResult: (res) => res.data,
    }),
  postCashFlowUpload: (params, config) =>
    http.post('/proj/review/cashflowplan/upload', params, {
      type: 'upload',
      transformResult: (res) => res.data,
      ...config,
      timeout: 0,
    }),
  postCashFlowUDownload: (params) =>
    http('/proj/review/cashflowplan/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  postCashFlowExport: (params) =>
    http.post('/proj/review/cashflowplan/cashflow/export', params, {
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  postRentExport: (params) =>
    http.post('/proj/review/cashflowplan/rent/export', params, {
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  postOnlyOfficeParams: (params) => http.post('/onlyoffice/docDetail', params),
}
