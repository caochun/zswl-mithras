import { http } from '@zswl/admin'

const mock = false

export default {
  postClientDetail: (data) => http.post('/rating/client/detail', data, { mock }),
  postClientOverturn: (data) => http.post('/rating/client/overturn', data, { mock }),
}
