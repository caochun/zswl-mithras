import { http } from '@zswl/admin'

export default {
  searchFounder: (params, { functionCode }) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: functionCode || 'selectfounder-2',
      },
    }),

  getClientList: (params, { functionCode }) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        timeout: 0,
        headers: {
          functionCode: functionCode || 'clientlist-2',
        },
      }
    ),

  getOrgList: (params, { functionCode }, url) =>
    http.get(url || '/select/orgs', {
      params,
      headers: {
        functionCode: functionCode || 'selectorgs-2',
      },
    }),

  getOrgList2: (params, { functionCode }) =>
    http.get('/client/select/orgs', {
      params,
      headers: {
        functionCode: functionCode || 'selectorgs-2',
      },
    }),

  getAllIndustry: (params) => http.get('/select/industry/all', { params }),
  getRegionList: (params) => http.get('/select/region/child', { params }),
}
