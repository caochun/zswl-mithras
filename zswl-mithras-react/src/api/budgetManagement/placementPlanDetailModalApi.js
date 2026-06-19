import { http } from '@zswl/admin'

export default {
  getOrgListByUserId: (params) => http.get('/select/orgs/byUserId', { params }),
  getBusinessHeaderByDeptId: (params) => http.get('/select/businessheader/byDeptId', { params }),
  importCashFlow: (params) =>
    http.post('/budget/plan/pay/notmonth/detail/cashFlow/parse', params, {
      type: 'upload',
      transformResult: (res) => res.data,
    }),
}
