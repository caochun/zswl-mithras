import { http } from '@zswl/admin'

const mock = false

export default {
  queryCompany: (data: any): Promise<any> =>
    http.post('/ledger/appraisal/queryCompany', data, { mock }),
}
