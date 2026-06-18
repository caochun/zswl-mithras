import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contract/rent/estimate/list', params),
  getListCompare: (params) => http.post('/contract/rent/estimate/list/compare', params),
  // 导入概算租金表
  postImportEstimate: (params) =>
    http.post('/contract/rent/estimate/import', params, {
      type: 'upload',
      timeout: 0,
    }),

  // 导出概算租金表
  postExportRentData: (params) =>
    http.post('/contract/rent/estimate/export', params, {
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  // 导出概算现金流表
  postExportCashData: (params) =>
    http.post('/contract/cashflow/estimate/export', params, {
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),

  postEstimateGenerate: (params) => http.post('/contract/rent/estimate/generate', params),
}
