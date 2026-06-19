import { http } from '@zswl/admin'

const mock = false

export default {
  postVersionList: (data?: any): Promise<any> =>
    http.post('/group/credit/review/version/list', data, { mock }),

  postComparePreVersion: (data?: any): Promise<any> =>
    http.post('/group/credit/review/compare/preVersion', data, { mock }),
}
