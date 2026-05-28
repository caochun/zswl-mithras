import { http } from '@zswl/admin'

export default {
  writeoffList: (params) => http.post('/margin/writeoff/list', params),
}
