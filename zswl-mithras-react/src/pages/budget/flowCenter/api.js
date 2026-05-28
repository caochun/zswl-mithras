import { http } from '@zswl/admin'

export default {
  //   付款手工核销
  manualRecord: (params) =>
    http.post('/collection/flow/center/business/payment/manual/record', params),

  // 付款业务流水列表
  paymentList: (params) => http.post('/collection/flow/center/business/payment/list', params),
  // 付款业务流水结算明细
  paymentSettleDetail: (params) =>
    http.post('/collection/flow/center/business/payment/settle/detail', params),
  // 收款业务流水列表
  collectionList: (params) => http.post('/collection/flow/center/business/collection/list', params),
  // 收款手工核销
  collectionManualRecord: (params) =>
    http.post('/collection/flow/center/business/collection/manual/record', params),
  // 收款业务流水结算明细
  collectionSettleDetail: (params) =>
    http.post('/collection/flow/center/business/collection/settle/detail', params),

  // 根据id查询流水
  postPaymentCashflowList: (params) =>
    http.post('/bank/center/finance/payment/cashflow/list', params),

  // 获取子列表信息
  postSubList: (data) => http.post('/bank/center/finance/sub/list', data),

  // 银行流水还原至处理中心
  postBankCenterRestore: (data) => http.post('/bank/center/restore', data),
}
