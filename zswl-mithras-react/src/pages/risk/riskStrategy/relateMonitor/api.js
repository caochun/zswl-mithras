/* prettier-ignore-start */
import { http } from '@zswl/admin'

export default {
  postCollectionList: (params) => http.post('/risk/control/related/transaction/collection', params),
  postPaymentList: (params) => http.post('/risk/control/related/transaction/payment', params),
  postClientPullDown: (params) => http.post('/risk/control/related/client/pulldown', params),
  postClientAdd: (params) =>
    http.post('/risk/control/related/client/add', params, {
      type: 'upload',
      timeout: 0,
    }),
}
