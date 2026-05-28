import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contract/account/list', params),
  getListCompare: (params) => http.post('/contract/account/list/compare', params),
  addItem: (params) => http.post('/contract/account/add', params, {}),
  updateItem: (params) => http.post('/contract/account/modify', params, {}),
  removeItem: (params) => http.post('/contract/account/remove', params, {}),
}
