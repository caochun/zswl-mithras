import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/profitadjust/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/profitadjust/save', params, {}),
}
