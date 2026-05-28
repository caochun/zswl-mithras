import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/financialmarketdeptassess/get', params),
  saveList: (params) => http.post('/kpi/parameterconfig/financialmarketdeptassess/save', params),
}
