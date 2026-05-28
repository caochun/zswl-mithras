import { http } from '@zswl/admin'

export default {
  getList: (params, header) => http.post('/cr/account/list', params, header),
  modifyItem: (params) => http.post('/cr/account/modify', params),
  reportItem: (params) => http.post('/cr/account/reportChange', params),
  reportChange: (params) => http.post('/cr/account/reportChange', params),
}
