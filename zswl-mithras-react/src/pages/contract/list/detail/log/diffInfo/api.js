import { http } from '@zswl/admin'

export default {
  compareVersionList: (params) => http.post('/contract/compare/preVersion', params),
}
