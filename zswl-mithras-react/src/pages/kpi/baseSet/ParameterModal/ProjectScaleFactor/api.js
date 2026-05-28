import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/projectradio/scale/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/projectradio/scale/save', params, {}),
}
