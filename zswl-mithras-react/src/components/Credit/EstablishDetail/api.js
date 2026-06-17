import { http } from '@zswl/admin'

export default {
  postProjectBaseInfoDetail: (params) => http.post('/proj/establish/base/info/detail', params),
  postProjectBaseInfoModify: (params) =>
    http.post('/proj/establish/base/info/modify', params, {
      transformResult: (res) => res.data,
    }),
  postProjectBaseInfoExposure: (params) => http.post('/proj/establish/base/info/exposure', params),
  submitApproval: (params) =>
    http.post(
      '/proj/establish/effect',
      { ...params },
      {
        transformResult: (res) => res.data,
      }
    ),
  postBaseInfoUpdateRating: (params) =>
    http.post('/group/credit/establish/base/info/updateRating', params, {}),
  ratingCheck: (params) => http.post('/group/credit/establish/rating/check', params, {}),
}
