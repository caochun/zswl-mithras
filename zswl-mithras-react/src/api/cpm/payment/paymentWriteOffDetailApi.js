import { http } from '@zswl/admin'

export default {
  getPaymentWriteoffDetail: (params) => http.post('/payment/detail/writeoff', params),
  getPaymentWriteoffActualDetail: (params) => http.post('/payment/list/actualdetail', params),
  getClientBankList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist-2',
      },
    }),
  postPaymentWriteoffActualDetailAdd: (params) =>
    http.post('/payment/actualdetail/add', params, {}),
  postPaymentWriteoffActualDetailModify: (params) =>
    http.post('/payment/actualdetail/modify', params, {}),
  postPaymentWriteoffActualDetailSubmit: (params) =>
    http.post('/payment/actualdetail/submit', params, {}),
  postPaymentWriteoffActualDetailDetail: (params) =>
    http.post('/payment/remove/actualdetail', params, {}),
  postPaymentReviewinadvancedSubmit: (params) =>
    http.post('/payment/reviewinadvanced/submit', params, {}),
  postPayMentWriteoffModify: (params) => http.post('/payment/writeoff/actualdetail', params, {}),
  postPayMentWriteoffDetail: (params) => http.post('/payment/detail/actualdetail', params),
  postPayMentWriteoff: (params) => http.post('/payment/writeoff', params, {}),
  postPayMentWriteoffUnDo: (params) => http.post('/payment/undowriteoff', params, {}),
  postPayMentWriteoffHistory: (params) => http.post('/payment/write/off/history/list', params),
  postPayMentCollectionAdd: (params) => http.post('/payment/collection/add', params),
  getPayMentCollectionDetail: (params) => http.get('/payment/collection/detail', { params }),
  postSendAdvanceApplication: (params) =>
    http.post('/payment/send/advance/application', params, {
      transformResult: (res) => res.data,
    }),
  updateCollectionDay: (params) => http.post('/payment/collection/day/modify', params),
}
