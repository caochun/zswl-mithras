import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/careerlevel/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/careerlevel/save', params, {}),
}
