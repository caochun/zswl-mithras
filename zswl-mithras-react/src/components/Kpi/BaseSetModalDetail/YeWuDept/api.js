import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/businessdeptassess/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/businessdeptassess/save', params, {}),
}
