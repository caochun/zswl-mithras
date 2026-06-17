import { http } from '@zswl/admin'

export default {
  getList: (params, header) => http.post('/cr/repay/list', params, header),
  modifyItem: (params) => http.post('/cr/repay/modify', params),
}
