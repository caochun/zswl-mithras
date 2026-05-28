import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/task/myProcess/withdraw/list', params),
  cancelProcess: (params) => http.post('/flow/execution/cancelProcess', params),
}
