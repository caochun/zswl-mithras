import { http } from '@zswl/admin'

export default {
  getVersionList: (params) => http.post('/proj/establish/version/list', params),
  compareVersionList: (params) => http.post('/proj/establish/compare/preVersion', params),
}
