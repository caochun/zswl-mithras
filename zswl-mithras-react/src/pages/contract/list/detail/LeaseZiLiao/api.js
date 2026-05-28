import { http } from '@zswl/admin'

export default {
  postLeaseFileList: (params) => http.post('/materials/contract/lease/list', params),
}
