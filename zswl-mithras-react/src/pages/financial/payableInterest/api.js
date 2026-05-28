import { http } from '@zswl/admin'

export default {
  postInterestPayList: (data) => http.post('/interestPay/list', data),
  postInterestPayCalculate: (data) =>
    http.post('/interestPay/calculate', data, {
      timeout: 0,
    }),
  postInterestBasicDetail: (data) => http.post('/interestPay/basic/detail', data),
  postInterestCalDetail: (data) => http.post('/interestPay/cal/detail', data),
  postInterestCalDetailModify: (data) => http.post('/interestPay/cal/detail/modify', data),
}
