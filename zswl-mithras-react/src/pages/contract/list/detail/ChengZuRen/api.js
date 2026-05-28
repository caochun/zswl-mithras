import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contract/tenantry/list', params),
  getListCompare: (params) => http.post('/contract/tenantry/list/compare', params),
  updateItem: (params) =>
    http.post('/contract/tenantry/modify', params, { type: 'upload', timeout: 0 }),
  getContactList: (params) => http.post('/corp/contact/old/list', params),
  getHighSeasCustomersList: (params) => http.post('/high/seas/customers/list', params),
}
