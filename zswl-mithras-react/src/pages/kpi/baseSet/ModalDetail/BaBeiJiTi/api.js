import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/provisionradio/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/provisionradio/save', params, {}),
}
