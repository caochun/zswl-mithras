import { http } from '@zswl/admin'

const mock = false

export default {
  postAmountReport: (data) => http.post('/rating/amount/report', data, { mock }),
}
