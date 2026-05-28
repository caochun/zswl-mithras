import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/parameterconfig/pagelist', params, {}),
}
