import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/projectradio/type/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/projectradio/type/save', params, {}),
}
