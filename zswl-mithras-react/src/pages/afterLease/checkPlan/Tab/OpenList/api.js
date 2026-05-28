import { http } from '@zswl/admin'

export default {
  getExternalList: (params) => http.post('/afterlease/check/external/query/list', params),
  // list页统计信息
  getStatistics: (params) =>
    http.post('/afterlease/check/external/query/list/statistics', params, {}),
}
