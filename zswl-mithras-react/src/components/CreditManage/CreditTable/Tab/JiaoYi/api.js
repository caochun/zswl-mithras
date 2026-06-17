import { http } from '@zswl/admin'

export default {
  getList: (params, header) => http.post('/cr/special/trade/list', params, header),
  modifyItem: (params) => http.post('/cr/special/trade/modify', params),
  reportItem: (params) => http.post('/cr/special/trade/reportChange', params),
  reportChange: (params) => http.post('/cr/account/reportChange', params),
}
