import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/other/filingMaterial/list', params),
  getReviewProject: (params) => http.post('/other/filingMaterial/getReviewProject', params),
  confirmProject: (params) => http.get('/other/filingMaterial/select/confirm', { params }),
  export: (params) =>
    http.post(
      '/other/filingMaterial/export',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),
  getOrgList: (params, { functionCode }) =>
    http.get('/select/level/orgs', {
      params,
      headers: {
        functionCode: functionCode || 'selectorgs-2',
      },
    }),
}
