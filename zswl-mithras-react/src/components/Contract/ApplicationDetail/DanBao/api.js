import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contract/guarantor/list', params),
  getListCompare: (params) => http.post('/contract/guarantor/list/compare', params),
  generateContractNo: (params) =>
    http.post('/contract/guarantor/contractcode/generate', params, {}),
  addItem: (params) => http.post('/contract/guarantor/add', params, { type: 'upload', timeout: 0 }),
  updateItem: (params) =>
    http.post('/contract/guarantor/modify', params, { type: 'upload', timeout: 0 }),
  removeItem: (params) => http.post('/contract/guarantor/remove', params, {}),
  postContractList: (params) => http.post('/contract/guarantor/relation/contract', params),
  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        headers: {
          functionCode: 'clientlist-3',
        },
      }
    ),
  getContactList: (params) => http.post('/corp/contact/old/list', params),
}
