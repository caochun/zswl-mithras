import { http } from '@zswl/admin'

const mock = false

export default {
  postCenterList: (data) => http.post('/bank/center/list', data, { mock }),
  postCollectionFlowCenterBusinessPaymentManualCashFlowList: (data) =>
    http.post('/collection/flow/center/business/payment/manual/cashFlowList', data, { mock }),
  postManualRecord: (data) =>
    http.post('/collection/flow/center/business/payment/manual/record', data, { mock }),
  postPullFlow: (data) => http.post('/bank/center/manual/pull/flow', data, { mock }),
  postWriteOff: (data) => http.post('/bank/center/batch/write/off', data, { mock }),
}
