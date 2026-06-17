import { http } from '@zswl/admin'

export default {
  postDataList: (params) => http.post('/materials/contract/list', params),
}
