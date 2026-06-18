import { http } from '@zswl/admin'

const mock = false

export default {
  postPrintingDetail: (data) => http.post('/printing/detail', data, { mock }),
}
