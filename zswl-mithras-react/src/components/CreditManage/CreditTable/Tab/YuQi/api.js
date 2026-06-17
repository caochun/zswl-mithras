import { http } from '@zswl/admin'

export default {
  getList: (params, header) => http.post('/cr/overdue/record/list', params, header),
  modifyItem: (params) => http.post('/cr/overdue/record/modify', params),
  deleteItem: (params) => http.post('/cr/overdue/record/remove', params),
  cancelItem: (params) => http.post('/cr/overdue/record/cancel/remove', params),
  reportChange: (params) => http.post('/cr/account/reportChange', params),
}
