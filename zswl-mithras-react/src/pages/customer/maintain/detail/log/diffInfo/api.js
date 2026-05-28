import { http } from '@zswl/admin'

export default {
  compareVersionList: (params) => http.post('/client/version/compare/preVersion', params),
}
