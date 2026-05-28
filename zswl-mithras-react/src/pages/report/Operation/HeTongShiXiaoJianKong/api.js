import { http } from '@zswl/admin'

export default {
  // 合同时效监控表-统计列表
  statisticList: (params) => http.post('/managereport/hetongshixiao/statistic', params, {}),
  // 合同时效监控表-明细列表
  detail: (params) => http.post('/managereport/hetongshixiao/detail', params, {}),
}
