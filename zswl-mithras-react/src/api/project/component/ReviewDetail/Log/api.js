import { http } from '@zswl/admin'

export default {
  getVersionList: (params) => http.post('/proj/review/version/list', params),
  compareVersionList: (params) => http.post('/proj/review/compare/preVersion', params),
}
