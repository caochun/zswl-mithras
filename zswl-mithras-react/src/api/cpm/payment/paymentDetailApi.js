import { http } from '@zswl/admin'

export default {
  getPaymentDetail: (params) => http.post('/payment/detail', params),
}
