import { http } from '@zswl/admin'

export default {
  contractClientBusinessCompare: (params) =>
    http.post('/contract/client/compare/business', params, {}),
  paymentClientBusinessCompare: (params) =>
    http.post('/payment/client/compare/business', params, {}),
  creditSearchClientBusinessCompare: (params) =>
    http.post('/creditreport/base/creditSearch/client/compare/business', params, {}),
  clientBusinessOpinionAdd: (params) => http.post('/client/business/opinion/add', params, {}),
  clientBusinessOpinionList: (params) => http.post('/client/business/opinion/list', params, {}),
}
