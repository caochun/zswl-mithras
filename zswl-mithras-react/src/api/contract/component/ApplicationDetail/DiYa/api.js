import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contract/mortgage/list', params),
  getListCompare: (params) => http.post('/contract/mortgage/list/compare', params),
  generateContractNo: (params) => http.post('/contract/mortgage/contractcode/generate', params, {}),
  addItem: (params) =>
    http.post('/contract/mortgage/add', params, {
      type: 'upload',
      timeout: 0,
    }),
  updateItem: (params) =>
    http.post('/contract/mortgage/modify', params, {
      type: 'upload',
      timeout: 0,
    }),
  removeItem: (params) => http.post('/contract/mortgage/remove', params),
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
  postContractList: (params) => http.post('/contract/mortgage/relation/contract', params),
  postDownloadTemp: (params) =>
    http('/contract/mortgage/template/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
    }),
}
