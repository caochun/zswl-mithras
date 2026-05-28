import { http } from '@zswl/admin'

export default {
  getDetail: (params) => http.post('/proj/lifecycle/detail', params),
  getProjEstablishDetail: (params) => http.post('/proj/lifecycle/projestablish/card', params),
  getProjReviewDetail: (params) => http.post('/proj/lifecycle/projreview/card', params),
  getProjContractDetail: (params) => http.post('/proj/lifecycle/contract/card', params),
  getMilestone: (params) => http.post('/proj/lifecycle/milestone', params),
  getRentCollection: (params) => http.post('/proj/lifecycle/rentcollection/card', params),
  getAfterLeaseCheck: (params) => http.post('/proj/lifecycle/afterleasecheck/card', params),
  getCommerceDetail: (params) => http.post('/corp/commerce/detail', params),
  getCommerceAddressList: (params) => http.post('/corp/address/list', params),
  getGeneralDetail: (params) => http.post('/assetclassify/client/general/list', params),
}
