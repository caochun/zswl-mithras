import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/projectradio/payment/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/projectradio/payment/save', params, {}),
}
