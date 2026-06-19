import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contract/pledge/list', params),
  getListCompare: (params) => http.post('/contract/pledge/list/compare', params),
  generateContractNo: (params) => http.post('/contract/pledge/contractcode/generate', params, {}),
  addItem: (params) =>
    http.post('/contract/pledge/add', params, {
      type: 'upload',
      timeout: 0,
    }),
  updateItem: (params) =>
    http.post('/contract/pledge/modify', params, {
      type: 'upload',
      timeout: 0,
    }),
  removeItem: (params) => http.post('/contract/pledge/remove', params, {}),
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
  postDownloadTemp: (params) =>
    http('/contract/pledge/template/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
  postContractList: (params) => http.post('/contract/pledge/relation/contract', params),
}
