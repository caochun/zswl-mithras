import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/proj/review/base/info/list', params),
  getProjectList: (params) => http.post('/proj/review/establish/query', params),

  postProjectReview: (params) =>
    http.post('/proj/review/base/info/add', params, {
      transformResult: (res) => res.data,
    }),
  postAddCreditReview: (params) =>
    http.post('/proj/review/base/info/addByGroupCredit', params, {
      transformResult: (res) => res.data,
    }),
  remove: (params) => http.post('/proj/review/base/info/disable', params),

  getEstablishList: (params) =>
    http.post('/proj/review/group/credit/review/query', params, {
      headers: {
        functionCode: 'projReviewGroupCreditReviewQuery',
      },
    }),
  getRemianCreditAmount: (params) => http.post('/group/credit/review/remainCreditAmount', params),
}
