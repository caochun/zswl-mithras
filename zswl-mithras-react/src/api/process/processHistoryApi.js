import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/process/history', params),
}
