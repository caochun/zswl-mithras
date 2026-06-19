import { http } from '@zswl/admin'

export default {
  getVersionList: (params) => http.post('/proj/pricing/version/list', params),
  compareVersionList: (params) => http.post('/proj/pricing/compare/preVersion', params),
}
