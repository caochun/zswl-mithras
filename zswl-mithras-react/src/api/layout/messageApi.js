import { http } from '@zswl/admin'

export default {
  contractRentNoticeDelay: (params) => http.post('/contract/start/rent/delay', params),
  postReadMessage: (params) => http.post('/message/read', params),
}
