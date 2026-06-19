import { http } from '@zswl/admin'

export default {
  getContractDetail: (params) => http.get('/contract/base/info/detail', { params }),
  getContractQSDetail: (params) => http.post('/contract/price/detail', params),
  getContractQSDetailCompare: (params) => http.post('/contract/price/detail/compare', params),
  getLatestPlanDetail: (params) => http.post('/contract/settle/plan/latest/get', params),

  getPaymentDetail: (params) => http.post('/payment/detail', params),
  getPaymentPolicyInfoList: (params) => http.post('/payment/policy/info/list', params),

  projectPricingBaseInfoCompare: (params) =>
    http.post('/proj/pricing/base/info/detail/compare', params),
  projectPricingQSCompare: (params) => http.post('/proj/pricing/price/detail/compare', params),
  projectPricingCashflowCompare: (params) =>
    http.post('/proj/pricing/cashflowplan/list/compare', params),

  projectReviewBaseInfoCompare: (params) =>
    http.post('/proj/review/base/info/detail/compare', params),
  projectReviewQSCompare: (params) => http.post('/proj/review/price/detail/compare', params),
  projectReviewCashflowCompare: (params) =>
    http.post('/proj/review/cashflowplan/list/compare', params),
}
