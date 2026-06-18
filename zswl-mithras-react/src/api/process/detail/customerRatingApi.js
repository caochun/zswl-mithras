import { http } from '@zswl/admin'

const mock = false

export default {
  postClientDetail: (data) => http.post('/rating/client/detail', data, { mock }),
  postClientReport: (data) => http.post('/rating/client/report', data, { mock }),
}
