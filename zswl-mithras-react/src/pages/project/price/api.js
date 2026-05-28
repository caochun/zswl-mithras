import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/proj/pricing/base/info/list', params),
  getProjectList: (params) => http.post('/proj/pricing/establish/query', params),

  postPricingCreate: (params) => http.post('/proj/pricing/create', params),
  postProjectReview: (params) =>
    http.post('/proj/pricing/base/info/add', params, {
      transformResult: (res) => res.data,
    }),
  // 授信
  postAddCreditReview: (params) =>
    http.post('/proj/pricing/base/info/addByGroupCredit', params, {
      transformResult: (res) => res.data,
    }),
  remove: (params) => http.post('/proj/pricing/base/info/disable', params),

  getEstablishList: (params) =>
    http.post('/proj/review/group/credit/review/query', params, {
      headers: {
        functionCode: 'projPricingProjReviewGroupCreditReviewQuery',
      },
    }),
  getRemianCreditAmount: (params) => http.post('/group/credit/review/remainCreditAmount', params),
}
