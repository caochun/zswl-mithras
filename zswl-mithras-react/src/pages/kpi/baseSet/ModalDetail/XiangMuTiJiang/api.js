import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/projectradio/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/projectradio/save', params, {}),
}
