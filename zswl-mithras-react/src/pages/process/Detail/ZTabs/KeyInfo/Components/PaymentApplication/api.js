import { http } from '@zswl/admin'

export default {
  getDetail: (params) => http.post('/payment/detail', params),
  getList: (params) => http.post('/payment/policy/info/list', params),
}
