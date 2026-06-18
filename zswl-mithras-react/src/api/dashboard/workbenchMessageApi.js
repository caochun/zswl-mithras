import { http } from '@zswl/admin'

export default {
  postMessageList: (params) => http.post('/message/list', params),
  postReadMessage: (params) => http.post('/message/read', params),
  postReadAllMessage: (params) => http.post('/message/read/all', params),
}
