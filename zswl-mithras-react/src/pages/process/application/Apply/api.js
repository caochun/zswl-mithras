import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/task/myProcess/applying/list', params),
  //撤回
  withdrawToStartUser: (params) => http.post('/flow/execution/withdrawToStartUser', params),
  cancelProcess: (params) => http.post('/flow/execution/cancelProcess', params),

  // 付款核销校验
  postPayMentCloseBeforeCheck: (params) =>
    http.post('/payment/close/before/check', params, { transformResult: (res) => res.data }),
}
