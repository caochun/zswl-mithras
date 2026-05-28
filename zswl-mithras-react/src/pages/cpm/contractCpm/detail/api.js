import { http } from '@zswl/admin'

export default {
  contractDetail: (params) => http.post('/contractcp/contract/detail', params),
  // contractList: (params) => http.post('/contractcp/contract/list', params),
  // //现金列表
  // contractList: (params) => http.post('/contractcp/cash/list', params),
  //
}
