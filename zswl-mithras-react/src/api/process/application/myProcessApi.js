import { http } from '@zswl/admin'

export default {
  myProcessCount: (params) => http.get('/flow/task/myProcess/count', { params }),

  getApplyingList: (params) => http.post('/flow/task/myProcess/applying/list', params),
  getFinishList: (params) => http.post('/flow/task/myProcess/finish/list', params),
  getWithdrawList: (params) => http.post('/flow/task/myProcess/withdraw/list', params),
  getBackToStepList: (params) => http.post('/flow/task/myProcess/backToStep/list', params),

  withdrawToStartUser: (params) => http.post('/flow/execution/withdrawToStartUser', params),
  cancelProcess: (params) => http.post('/flow/execution/cancelProcess', params),

  getPrepareList: (params) => http.post('/process/prepare/list', params),
  getPrepareDetail: (params) => http.post('/process/prepare/detail', params),
  discardPrepare: (params) => http.post('/process/prepare/discard', params),
  commitPrepare: (params) => http.post('/process/prepare/commit', params),

  checkFilingMaterialFile: (params) => http.post('/fund/filingMaterial/checkFile', params),
  submitProjectDistribution: (data) =>
    http.post('/finance/projectdistribution/submit', data, { mock: false }),
  noticeDepositCommit: (prepareId) =>
    http.post(`/contract/depost/noticeCommit?prepareId=${prepareId}`),

  postPayMentCloseBeforeCheck: (params) =>
    http.post('/payment/close/before/check', params, { transformResult: (res) => res.data }),
}
