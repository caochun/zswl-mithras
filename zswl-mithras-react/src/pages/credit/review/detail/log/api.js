import { http } from '@zswl/admin'

export default {
  getVersionList: (params) => http.post('/group/credit/review/version/list', params),
  compareVersionList: (params) => http.post('/group/credit/review/compare/preVersion', params),
}
