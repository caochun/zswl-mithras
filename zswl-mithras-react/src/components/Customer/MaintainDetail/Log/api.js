import { http } from '@zswl/admin'

export default {
  getVersionList: (params) => http.post('/client/version/list', params),
  compareVersionList: (params) => http.post('/client/version/compare/preVersion', params),
}
