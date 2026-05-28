import { http } from '@zswl/admin'

export default {
  marginDetail: (params) => http.post('/margin/detail', params),
}
