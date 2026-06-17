import { http } from '@zswl/admin'

export default {
  getBaseInfo: (params) => http.post('/group/credit/review/base/info/detail', params, {}),
  getBaseInfoCompare: (params) =>
    http.post('/group/credit/review/base/info/detail/compare', params, {}),
  postBaseInfoModify: (params) => http.post('/group/credit/review/base/info/modify', params, {}),
  submit: (params) => http.post('/group/credit/review/effect', params, {}),
  postBaseInfoUpdateRating: (params) =>
    http.post('/group/credit/review/base/info/updateRating', params, {}),
  ratingCheck: (params) => http.post('/group/credit/review/rating/check', params, {}),
}
