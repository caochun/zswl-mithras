import { http } from '@zswl/admin'

export default {
  getDetail: (params) => http.get('/contract/base/info/detail', { params }),
  getContractQSDetail: (params) => http.post('/contract/price/detail', params),
  getContractQSDetailCompare: (params) => http.post('/contract/price/detail/compare', params),
  getLatestPlanDetail: (params) => http.post('/contract/settle/plan/latest/get', params),
}
