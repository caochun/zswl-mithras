import { http } from '@zswl/admin'

export default {
  getContractQSDetail: (params) => http.post('/contract/price/detail', params),
  getContractQSDetailCompare: (params) => http.post('/contract/price/detail/compare', params),
  postContractQSModify: (params) => http.post('/contract/price/modify', params, {}),
  getLprLast: (params) => http.post('/basedata/lpr/latest', params),
  postIRRCalculate: (params) => http.post('/contract/rent/estimate/irr/calculate', params),
  postPhaseList: (params) => http.post('/contract/price/phase/list', params),
}
