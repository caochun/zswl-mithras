import { http } from '@zswl/admin'

export default {
  getVersionList: (params) => http.post('/contract/version/list', params),
  compareVersionList: (params) => http.post('/contract/compare/preVersion', params),
}
