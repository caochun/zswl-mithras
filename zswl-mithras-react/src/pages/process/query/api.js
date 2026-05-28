import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/process/list', params),
  searchOrgs: (params) =>
    http.get('/select/orgs', {
      params,
      headers: {
        functionCode: 'selectorgs-2',
      },
    }),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),
  //跳转
  jump: (data) => http.post('/flow/execution/jump', data),

  //转办
  forward: (data) => http.post('/flow/execution/transfer', data),
  //用户列表
  getUserList: (params) => http.get('/user/list', { params }),
  //一键通过
  pass: (data) => http.post('/flow/execution/passAll', data),

  //一键拒绝
  reject: (data) => http.post('/flow/execution/rejectAll', data),

}
