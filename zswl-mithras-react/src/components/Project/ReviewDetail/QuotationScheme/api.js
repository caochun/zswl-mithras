import { http } from '@zswl/admin'

export default {
  postProjectQSDetail: (params) => http.post('/proj/review/price/detail', params),
  postProjectQSDetailCompare: (params) => http.post('/proj/review/price/detail/compare', params),
  postProjectPricingQSDetailCompare: (params) =>
    http.post('/proj/review/pricing/price/detail/compare', params),
  postProjectQSModify: (params) =>
    http.post('/proj/review/price/modify', params, {
      transformResult: (res) => res.data,
    }),
}
