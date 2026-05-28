import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/proj/establish/base/info/list', params),
  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        headers: {
          functionCode: 'clientlist-1',
        },
      }
    ),
  postProject: (params) =>
    http.post('/proj/establish/base/info/add', params, {
      transformResult: (res) => res.data,
    }),
  remove: (params) => http.post('/proj/establish/base/info/disable', params),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-4',
      },
    }),
  searchOrgs: (params) =>
    http.get('/select/orgs', {
      params,
      headers: {
        functionCode: 'selectorgs-3',
      },
    }),
}
