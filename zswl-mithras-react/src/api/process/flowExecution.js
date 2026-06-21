import { http } from '@zswl/admin'

export default {
  getVersionList: (params) => http.post('/proj/establish/version/list', params),
  compareVersionList: (params) => http.post('/proj/establish/compare/preVersion', params),
  cancelProcess: (params) => http.post('/flow/execution/cancelProcess', params),
  withdrawTask: (params) => http.post('/flow/execution/withdrawTask', params),
  reject: (params) => http.post('/flow/execution/reject', params),
  withdrawToStartUser: (params) => http.post('/flow/execution/withdrawToStartUser', params),
  backToStartUser: (params) => http.post('/flow/execution/backToStartUser', params),
  backToStep: (params) => http.post('/flow/execution/backToStep', params),
  collaborate: (params) => http.post('/flow/execution/collaborate', params, { timeout: 0 }),
  passProcess: (params) => http.post('/flow/execution/pass', params, { timeout: 0 }),
  //转办
  transfer: (data) => http.post('/flow/execution/transfer', data),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),

  // 付款核销校验
  postPaymentCloseBeforeCheck: (params) =>
    http.post('/payment/close/before/check', params, { transformResult: (res) => res.data }),

  // 校验申请金额
  postPaymentCheckApplyAmount: (params) => http.post('/payment/check/apply/amount', params),
  // 公开信息-检查是否存在客户没有维护公开信息(返回空数组即校验通过)
  publicCheck: (params) => http.post('/public/info/check', params),
}
