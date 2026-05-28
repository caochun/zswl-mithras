import { http } from '@zswl/admin'

export default {
  reviewBaseInfoCompare: (params) => http.post('/proj/review/base/info/detail/compare', params),
  reviewQSCompare: (params) => http.post('/proj/review/price/detail/compare', params),
  reviewCashflowCompare: (params) => http.post('/proj/review/cashflowplan/list/compare', params),
}
