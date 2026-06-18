import { http } from '@zswl/admin'

export default {
  paymentTimeOutCheck: (params) => http.post('/payment/projreview/timeout/check', params, {}),
  postPaymentFinanceCheckClientOpinion: (params) =>
    http.post('/payment/check/finance/project/distribution', params),
  getMonitorCount: (params) => http.post('/risk/control/opinion/monitor/count', params, {}),
}
