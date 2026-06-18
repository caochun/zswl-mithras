import { http } from '@zswl/admin'

export default {
  getProcessDetail: (params) => http.post('/flow/task/process/detail', params),
}
