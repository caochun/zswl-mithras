import { http } from '@zswl/admin'

export default {
  //业务工作台-运营部-时效统计-时间周期
  postDashboardOperationTimeTerm: (data) =>
    http.post('/dashboard/operation/conversion/term', data, {}),

  //业务工作台-运营部-时效统计-平均耗时(工作日)
  postDashboardOperationTimeStatistics: (data) =>
    http.post('/dashboard/operation/conversion/statistics', data),

  //业务工作台-运营视角-时效统计-详情
  postDashboardOperationTimeList: (data) =>
    http.post('/dashboard/operation/conversion/list', data, {}),
}
