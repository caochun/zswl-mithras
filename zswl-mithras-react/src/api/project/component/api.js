import { http } from '@zswl/admin'

// referer 被哪些页面模块引用
export default {
  getList: (params, { referer }) =>
    http.post('/proj/review/establish/query', params, {
      headers: {
        functionCode:
          {
            review: 'projreviewestablishquery',
            price: 'projPricingProjReviewEstablishQuery',
          }[referer] || 'projreviewestablishquery',
      },
    }),
  getClientList: (params, { referer }) =>
    http.post('/client/list', params, {
      headers: {
        functionCode:
          {
            review: 'clientlist-11',
            paymentApplication: 'clientlist-7',
          }[referer] || 'clientlist-10',
      },
    }),
  searchFounder: (params, { referer }) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode:
          {
            review: 'selectfounder-5',
            customer: 'selectfounder',
          }[referer] || 'selectfounder-4',
      },
    }),
  searchOrgs: (params, { referer }) =>
    http.get('/select/orgs', {
      params,
      headers: {
        functionCode:
          {
            review: 'selectorgs-4',
            customer: 'selectorgs',
          }[referer] || 'selectorgs-3',
      },
    }),
  postQueryEffect: (params) => http.post('/proj/review/query/effect', params),

  postProjectBaseInfoExposure: (params) => http.post('/proj/establish/base/info/exposure', params),
}
