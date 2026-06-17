import { http } from '@zswl/admin'

export default {
  postProjectBaseInfoDetail: (params) => http.post('/proj/review/base/info/detail', params),
  postProjectBaseInfoDetailCompare: (params) =>
    http.post('/proj/review/base/info/detail/compare', params),
  postProjectPricingBaseInfoDetailCompare: (params) =>
    http.post('/proj/review/pricing/base/info/detail/compare', params),
  messageNotice: (params) => http.post('/message/notice/manager', params),
  priceIrrSave: (params) => http.post('/proj/review/price/irr/save', params),
  postProjectBaseInfoModify: (params) => http.post('/proj/review/base/info/modify', params, {}),
  submitApproval: (params) => http.post('/proj/review/effect', { ...params }),
  submitPriseApproval: (params) => http.post('/proj/review/pricingApproval', { ...params }),
  postGetAddressByClientId: (params) =>
    http.post('/proj/establish/get/client/address', params, {
      headers: {
        functionCode: 'projReviewGetClientAddress',
      },
    }),
  sourceSceneValidate: (params) => http.post('/proj/review/client/materials/check', params),
  ratingCheck: (params) => http.post('/proj/review/rating/check', params),
  postProjectDataDetail: (params) => http.post('/materials/proj/review/list', params),
}
