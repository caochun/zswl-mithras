import { http } from '@zswl/admin'

export default {
  postProjectBaseInfoDetail: (params) => http.post('/proj/review/base/info/detail', params),
}
