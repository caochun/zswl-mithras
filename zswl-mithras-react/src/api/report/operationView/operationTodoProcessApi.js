import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/process/list', params),
}
