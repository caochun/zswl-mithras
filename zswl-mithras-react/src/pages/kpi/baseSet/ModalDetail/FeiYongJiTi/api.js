import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/expenseradio/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/expenseradio/save', params, {}),
}
