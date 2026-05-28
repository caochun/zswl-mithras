import { http } from '@zswl/admin'

export default {
  getProjectList: (params) => http.post('/proj/review/establish/query', params),
  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        headers: {
          functionCode: 'clientlist-9',
        },
      }
    ),

  remove: (params) => http.post('/proj/review/base/info/disable', params),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-5',
      },
    }),
  searchOrgs: (params) =>
    http.get('/select/orgs', {
      params,
      headers: {
        functionCode: 'selectorgs-4',
      },
    }),

  // 列表
  getList: (params) => http.post('/group/credit/review/base/info/list', params),
  // 集团授信立项模糊查询
  getEstablishList: (params) => http.post('/group/credit/review/establish/query', params),
  // 新增
  postProjectReview: (params) => http.post('/group/credit/review/base/info/add', params, {}),
}
