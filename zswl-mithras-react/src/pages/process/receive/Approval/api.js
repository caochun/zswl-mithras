import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/task/myReceive/done/list', params),
}
