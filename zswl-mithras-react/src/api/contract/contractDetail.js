import { http } from '@zswl/admin'

export default {
  submitApproval: (params) => http.post('/contract/flow/effect/submit', { ...params }, {}),
  submitChangeFlow: (params) => http.post('/contract/flow/change/submit', params, {}),
  priceIrrSave: (params) => http.post('/contract/price/irr/save', params, {}),
  calculateCombinedIrr: (params) =>
    http.post('/contract/rent/combined/irr/calculate', { ...params }, {}),
  cancelFlow: (params) => http.post('/contract/flow/change/cancel', params, {}),
  getBaseInfo: (params) => http.get('/contract/base/info/detail', { params }),
  getBaseInfoCompare: (params) => http.post('/contract/base/info/detail/compare', params),
  postBaseInfoModify: (params) => http.post('/contract/base/info/modify', params, {}),
  postChangeremarkModify: (params) =>
    http.post('/contract/base/info/changeremark/save', params, {}),
  checkIrr: (params) => http.post('/contract/operation/checkIrr', params, {}),
}
