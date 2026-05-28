import { http } from '@zswl/admin'

export default {
  getList: (params, header) => http.post('/cr/five/class/list', params, header),
  modifyItem: (params) => http.post('/cr/five/class/modify', params),
  addItem: (params) => http.post('/cr/five/class/add', params),
  deleteItem: (params) => http.post('/cr/five/class/remove', params),
  deleteCancelItem: (params) => http.post('/cr/five/class/cancel/remove', params),
  getZhangHuList: (params, header) => http.post('/cr/account/list', params, header),
}
