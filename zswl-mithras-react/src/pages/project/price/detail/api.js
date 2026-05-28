import { http } from '@zswl/admin'

export default {
  postProjectBaseInfoDetail: (params) => http.post('/proj/pricing/base/info/detail', params),
  postProjectBaseInfoDetailCompare: (params) =>
    http.post('/proj/pricing/base/info/detail/compare', params),
  postProjectPricingBaseInfoDetailCompare: (params) =>
    http.post('/proj/pricing/review/base/info/detail/compare', params),
  messageNotice: (params) => http.post('/message/notice/manager', params),
  priceIrrSave: (params) => http.post('/proj/pricing/price/irr/save', params),
  postProjectBaseInfoModify: (params) =>
    http.post('/proj/pricing/base/info/modify', params, {
      // transformResult: (res) => res.data,
    }),
  submitApproval: (params) => http.post('/proj/pricing/effect', { ...params }),
  submitPriseApproval: (params) => http.post('/proj/pricing/pricingApproval', { ...params }),

  postGetAddressByClientId: (params) =>
    http.post('/proj/establish/get/client/address', params, {
      headers: {
        functionCode: 'projReviewGetClientAddress',
      },
    }),
  postProjectDataDetail: (params) => http.post('/materials/proj/pricing/list', params),
}
