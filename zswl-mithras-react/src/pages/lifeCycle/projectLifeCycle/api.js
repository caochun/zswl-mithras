import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/proj/lifecycle/list', params),
  getQuantityCount: (params) => http('/proj/lifecycle/quantity/count', { params }),
  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        headers: {
          functionCode: 'clientlist-projectLifeCycle',
        },
      }
    ),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-projectLifeCycle',
      },
    }),
  searchOrgs: (params) =>
    http.get('/select/orgs', {
      params,
      headers: {
        functionCode: 'selectorgs-projectLifeCycle',
      },
    }),
}
