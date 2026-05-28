import { http } from '@zswl/admin'

export default {
  compareVersionList: (params) => http.post('/proj/establish/compare/preVersion', params),
}
