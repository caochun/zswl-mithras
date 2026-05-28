import { http } from '@zswl/admin'

export default {
  processDetail: (params) => http.post('/flow/task/process/detail', params),
  taskDetail: (params) => http.post('/flow/task/task/detail', params),
  establishBaseInfoCompare: (params) =>
    http.post('/proj/establish/base/info/detail/compare', params),
  establishQSCompare: (params) => http.post('/proj/establish/price/detail/compare', params),
  reviewBaseInfoCompare: (params) => http.post('/proj/review/base/info/detail/compare', params),
  reviewQSCompare: (params) => http.post('/proj/review/price/detail/compare', params),
  reviewCashflowCompare: (params) => http.post('/proj/review/cashflowplan/list/compare', params),

  reviewPricingBaseInfoCompare: (params) =>
    http.post('/proj/review/pricing/base/info/detail/compare', params),
  reviewPricingQSCompare: (params) =>
    http.post('/proj/review/pricing/price/detail/compare', params),
  reviewPricingCashflowCompare: (params) =>
    http.post('/proj/review/pricing/cashflowplan/list/compare', params),
  //项目定价
  pricingBaseInfoCompare: (params) => http.post('/proj/pricing/base/info/detail/compare', params),
  pricingQSCompare: (params) => http.post('/proj/pricing/price/detail/compare', params),
  pricingCashflowCompare: (params) => http.post('/proj/pricing/cashflowplan/list/compare', params),
  paymentCompare: (params) => http.post('/payment/baseinfo/compare', params),
  //获取客户按钮状态
  getButtonStatus: (params) => http.post('/client/button/status', params),

  //合同基本信息、报价信息
  contractBaseInfo: (params) => http.post('/contract/base/info/detail/compare', params),

  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),
  getTransferDetail: (params) => http.post('/client/transfer/detail', params),
  getContractIdByReduceId: (params) => http.post('/receipt/relation/contract', params),
  postFlowDownload: (params) => http.post('/archives/flow/download', params),
  archivesFlow: (params) => http.post('/archives/flow', params),
}
