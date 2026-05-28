import { http } from '@zswl/admin'

export default {
  checkStatistics: (params) => http.post('/dashboard/after/lease/check/statistics', params, {}),
  checkList: (params) => http.post('/dashboard/after/lease/check/list', params, {}),
}
