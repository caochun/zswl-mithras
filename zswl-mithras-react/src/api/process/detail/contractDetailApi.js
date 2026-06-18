import { http } from '@zswl/admin'

export default {
  getBaseInfo: (params) => http.get('/contract/base/info/detail', { params }),
}
