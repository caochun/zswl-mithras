import { http } from '@zswl/admin'

export default {
  postProjectQSDetail: (params) => http.post('/proj/establish/price/detail', params),
  postProjectQSDetailCompare: (params) => http.post('/proj/establish/price/detail/compare', params),
  postProjectQSModify: (params) =>
    http.post('/proj/establish/price/modify', params, {
      transformResult: (res) => res.data,
    }),
}
