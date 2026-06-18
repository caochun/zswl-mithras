import { http } from '@zswl/admin'

export default {
  modifyContractPayment: (params) => http.post('/payment/ftp/modify', params, { mock: false }),
}
