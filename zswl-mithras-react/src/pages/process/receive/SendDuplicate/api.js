import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/task/myReceive/cc/list', params),
}
