import { http } from '@zswl/admin'
export default {
  ///select/orgs/byUserId
  getOrgListByUserId: (params) => http.get('/select/orgs/byUserId', { params }),
  // /select/businessheader/byDeptId
  getBusinessHeaderByDeptId: (params) => http.get('/select/businessheader/byDeptId', { params }),
  // /budget/plan/pay/notmonth/detail/cashFlow/import
  importCashFlow: (params) =>
    http.post('/budget/plan/pay/notmonth/detail/cashFlow/parse', params, {
      type: 'upload',
      transformResult: (res) => res.data,
    }),
}
