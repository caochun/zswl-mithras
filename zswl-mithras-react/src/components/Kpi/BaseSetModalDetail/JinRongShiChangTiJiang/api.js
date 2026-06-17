import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/financialmarketdeptradio/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/financialmarketdeptradio/save', params, {}),
}
