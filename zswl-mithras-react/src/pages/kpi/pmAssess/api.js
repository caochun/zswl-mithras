import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/kpi/assessment/projmanager/pagelist', params),
  getDetail: (params) => http.post('/kpi/assessment/projmanager/detail/get', params),
  saveItem: (params) => http.post('/kpi/assessment/projmanager/detail/save', params),
  submit: (params) => http.post('/kpi/assessment/projmanager/flow/submit', params),
}
