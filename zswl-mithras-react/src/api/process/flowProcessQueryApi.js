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
  jump: (data) => http.post('/flow/execution/jump', data),
  forward: (data) => http.post('/flow/execution/transfer', data),
  getUserList: (params) => http.get('/user/list', { params }),
  pass: (data) => http.post('/flow/execution/passAll', data),
  reject: (data) => http.post('/flow/execution/rejectAll', data),
}
