import { http } from '@zswl/admin'

export default {
  getContractPaymentAll: (params) =>
    http.post('/payment/contractWrittenOffAmount/list', params, { mock: false }),
  modifyContractPayment: (params) => http.post('/payment/ftp/modify', params, { mock: false }),
  getContractPaymentFtp: (params) => http.post('/payment/ftp/get', params, { mock: false }),
}
