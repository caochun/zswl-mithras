import { http } from '@zswl/admin'

export default {
  getList: (params, header) => http.post('/cr/pledge/list', params, header),
  reportChange: (params) => http.post('/cr/account/reportChange', params),
}
