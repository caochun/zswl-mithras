import { http } from '@zswl/admin'

export default {
  getFinanceList: (params) =>
    http.post('/corp/subject/item/list', { ...params, sourceScene: 'AFTER_LEASE_CHECK_REPORT' }),
  uploadExcel: (params) => http.post('/corp/subject/item/import', params),
  downTemplate: (params) =>
    http.post(`/corp/subject/item/template?type=${params.type}`, params, {
      type: 'download',
      fileName: `${params.fileName}.xlsx`,
      timeout: 0,
    }),
  commerceDetail: (params) =>
    http.post('/corp/commerce/detail', { ...params, sourceScene: 'AFTER_LEASE_CHECK_REPORT' }),
  //客户生效（或提交审批）
  clientEffect: (params) => http.post('/client/effect', params),
}
