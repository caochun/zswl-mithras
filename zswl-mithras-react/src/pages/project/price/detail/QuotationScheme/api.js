import { http } from '@zswl/admin'

export default {
  postProjectQSDetail: (params) => http.post('/proj/pricing/price/detail', params), 
  postProjectQSDetailCompare: (params) => http.post('/proj/pricing/price/detail/compare', params),
  postProjectPricingQSDetailCompare: (params) =>
    http.post('/proj/pricing/review/price/detail/compare', params),
  postProjectQSModify: (params) =>
    http.post('/proj/pricing/price/modify', params, { 
      transformResult: (res) => res.data,
    }),
}
