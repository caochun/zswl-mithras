import { http } from '@zswl/admin'

export default {
  reviewBaseInfoCompare: (params) => http.post('/proj/pricing/base/info/detail/compare', params),
  reviewQSCompare: (params) => http.post('/proj/pricing/price/detail/compare', params),
  reviewCashflowCompare: (params) => http.post('/proj/pricing/cashflowplan/list/compare', params),
}
