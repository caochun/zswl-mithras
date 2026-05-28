import { http } from '@zswl/admin'

export default {
  compareVersionList: (params) => http.post('/proj/review/compare/preVersion', params),
}
