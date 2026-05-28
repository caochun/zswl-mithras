import { http } from '@zswl/admin'

export default {
  compareVersionList: (params) => http.post('/proj/pricing/compare/preVersion', params),
}
