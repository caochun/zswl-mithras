import { http } from '@zswl/admin'

export default {
  // 获取实际租金表
  getActualList: (params) => http.post('/contract/rent/actual/list', params),
  getActualListCompare: (params) => http.post('/contract/rent/actual/list/compare', params),

  // 导出实际租金表
  postExportRentData: (params) =>
    http.post('/contract/rent/actual/export', params, {
      type: 'download',
      timeout: 0,
    }),
  // 导出现金
  postExportCashData: (params) =>
    http.post('/contract/cashflow/actual/export', params, {
      type: 'download',
      timeout: 0,
    }),

  // 导入实际租金表
  postImportRent: (params) =>
    http.post('/contract/rent/actual/import', params, {
      type: 'upload',
      timeout: 0,
    }),

  // 删除实际租金表（删除借据）
  postRemoveReceipt: (params) => http.post('/contract/receipt/remove', params, {}),

  // 付款申请-列表接口
  postPaymentList: (params) => http.post('/payment/noreceipt/list', params),

  // 更新实际IRR
  postUpdateActualIRR: (params) => http.post('/contract/receipt/updateActualIRR', params),

  // 更新实际IRR
  postUpdateActualTax: (params) => http.post('/contract/receipt/updateActualTax', params),

  // 系统测算IRR
  postIRRCalclate: (params) => http.post('/contract/rent/actual/irr/calculate', params),

  // 税计算
  postQueryActualTax: (params) => http.post('/contract/receipt/queryActualTax', params),
}
