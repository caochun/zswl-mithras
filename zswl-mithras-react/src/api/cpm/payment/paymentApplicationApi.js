import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/payment/list', params),
  getContractList: (params) => http.post('/payment/contract/list', params),
  postPayment: (params) => http.post('/payment/add', params),
  remove: (params) => http.post('/payment/disable', params),

  getWriteOffList: (params) => http.post('/payment/list/writeoff', params),
  getPaymentWriteoffFinish: (params) => http.post('/payment/finish', params),
  getCollectionDay: (params) => http.post('/payment/collection/day', params),
  updateCollectionDay: (params) => http.post('/payment/collection/day/modify', params),
}
