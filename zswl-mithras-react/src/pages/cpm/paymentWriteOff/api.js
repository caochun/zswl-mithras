import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/payment/list/writeoff', params),
  getPaymentWriteoffFinish: (params) => http.post('/payment/finish', params),
  getCollectionDay: (params) => http.post('/payment/collection/day', params),
  updateCollectionDay: (params) => http.post('/payment/collection/day/modify', params),
}
