import { http } from '@zswl/admin'

export default {
  getBaseInfoSum: (params) => http.post('/fund/direct/financing/base/info/sum', params),
  getBaseInfoList: (params) => http.post('/fund/direct/financing/base/info/list', params),
  addBaseInfo: (params) => http.post('/fund/direct/financing/base/info/add', params),
  obsoleteBaseInfo: (params) => http.post('/fund/direct/financing/base/info/obsolete', params),
  deleteBaseInfo: (params) => http.post('/fund/direct/financing/base/info/delete', params),
  getBaseInfo: (params) => http.post('/fund/direct/financing/base/info/detail', params),
  editBaseInfo: (params) => http.post('/fund/direct/financing/base/info/modify', params),
  syncBaseInfo: (params) => http.post('/fund/direct/financing/base/info/sync', params),
  directDetailBaseInfo: (params) => http.post('/fund/receipt/repay/base/info/directDetail', params),
  getAsset: (params) => http.post('/fund/direct/financing/asset/pool/detail', params),
  editAsset: (params) => http.post('/fund/direct/financing/asset/pool/modify', params),
  getPledgeList: (params) => http.post('/fund/direct/financing/pledge/info/list', params),
  getPledgeDetail: (params) => http.post('/fund/direct/financing/pledge/info/detail', params),
  delPledge: (params) => http.post('/fund/direct/financing/pledge/info/remove', params),
  addPledge: (params) => http.post('/fund/direct/financing/pledge/info/add', params),
  editPledge: (params) => http.post('/fund/direct/financing/pledge/info/modify', params),
  exportPledge: (params) =>
    http.post('/fund/direct/financing/pledge/info/export', params, {
      type: 'download',
      timeout: 0,
    }),
  getPropertyList: (params) => http.post('/fund/financing/property/list', params),
  getProductList: (params) => http.post('/fund/direct/financing/product/list', params),
  getProductDetail: (params) => http.post('/fund/direct/financing/product/detail', params),
  delProduct: (params) => http.post('/fund/direct/financing/product/remove', params),
  addProduct: (params) => http.post('/fund/direct/financing/product/add', params),
  editProduct: (params) => http.post('/fund/direct/financing/product/modify', params),
  exportProduct: (params) =>
    http.post('/fund/direct/financing/product/export', params, {
      type: 'download',
      timeout: 0,
    }),
  getSubscribeList: (params) => http.post('/fund/direct/financing/subscription/list', params),
  delSubscribe: (params) => http.post('/fund/direct/financing/subscription/remove', params),
  addSubscribe: (params) => http.post('/fund/direct/financing/subscription/add', params),
  editSubscribe: (params) => http.post('/fund/direct/financing/subscription/modify', params),
  exportSubscribe: (params) =>
    http.post('/fund/direct/financing/subscription/export', params, {
      type: 'download',
      timeout: 0,
    }),
  getFinance: (params) => http.post('/fund/direct/financing/fee/list', params),
  getCostList: (params) => http.post('/fund/direct/financing/fee/detail', params),
  delCost: (params) => http.post('/fund/direct/financing/fee/remove', params),
  addCost: (params) => http.post('/fund/direct/financing/fee/add', params),
  editCost: (params) => http.post('/fund/direct/financing/fee/modify', params),
  editFinancing: (params) => http.post('/fund/direct/financing/programme/modify', params),
  postCalculate: (params) =>
    http.post('/fund/direct/financing/repay/actual/list/calculate', params),
  getRepayList: (params) => http.post('/fund/direct/financing/repay/actual/list', params),
  importRepay: (params) =>
    http.post('/fund/direct/financing/repay/actual/import', params, {
      type: 'upload',
      timeout: 0,
    }),
  exportRepay: (params) =>
    http.post('/fund/direct/financing/repay/actual/modify', params, {
      type: 'download',
      timeout: 0,
    }),
  getSplitList: (params) => http.post('/fund/direct/financing/repay/actual/split/list', params),
  getAccountList: (params) => http.post('/fund/direct/financing/pay/account/list', params),
  delAccount: (params) => http.post('/fund/direct/financing/pay/account/remove', params),
  addAccount: (params) => http.post('/fund/direct/financing/pay/account/add', params),
  editAccount: (params) => http.post('/fund/direct/financing/pay/account/modify', params),
  getDefaultValues: (params) => http.post('/basedata/bankaccount/init', params),
  download: (params) =>
    http.get('/fund/direct/financing/download', { params, type: 'download', timeout: 0 }),
  batchDownload: (params) =>
    http.post(
      '/fund/direct/financing/batchDownload',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),
}
