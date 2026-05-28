import { http } from '@zswl/admin'

export default {
  myReceiveCount: (params) => http.get('/flow/task/myReceive/count', { params }),
}
