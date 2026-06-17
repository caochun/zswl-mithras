import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/deptprofitfinishradio/get', params, {}),
  saveList: (params) => http.post('/kpi/parameterconfig/deptprofitfinishradio/save', params, {}),
}
