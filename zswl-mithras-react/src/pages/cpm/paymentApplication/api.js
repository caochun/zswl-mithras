import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/payment/list', params),
  getContractList: (params) => http.post('/payment/contract/list', params),
  postPayMent: (params) => http.post('/payment/add', params),
  remove: (params) => http.post('/payment/disable', params),
}
