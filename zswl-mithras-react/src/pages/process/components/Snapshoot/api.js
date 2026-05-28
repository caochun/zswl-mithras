import { http } from '@zswl/admin'

export default {
  processDetail: (params) => http.post('/flow/task/process/detail', params),
  taskDetail: (params) => http.post('/flow/task/task/detail', params),
}
