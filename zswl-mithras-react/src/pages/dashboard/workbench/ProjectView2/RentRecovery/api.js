import { http } from '@zswl/admin'

export default {
  // 项目情况
  // 业务工作台-项目视图-项目情况-统计
  postProjectInfoStatisticsRentInfoList: (data) =>
    http.post('/dashboard/project/info/statisticsRentInfoList', data),

  // 业务工作台-项目视图-项目情况-质押/监管情况
  postProjectInfoStatisticsPledgeList: (data) =>
    http.post('/dashboard/project/info/pledge/list', data),
  // 业务工作台-项目视图-项目情况-质押/监管情况-导出
  postProjectInfoStatisticsPledgeListExport: (data) =>
    http.post('/dashboard/project/info/pledge/list/export', data, { type: 'download' }),

  // 业务工作台-项目视图-项目情况-剩余本金与拨备
  postProjectInfoStatisticsProvisionList: (data) =>
    http.post('/dashboard/project/info/provision/list', data),
  // 业务工作台-项目视图-项目情况-剩余本金与拨备-导出
  postProjectInfoStatisticsProvisionListExport: (data) =>
    http.post('/dashboard/project/info/provision/list/export', data, { type: 'download' }),

  // 业务工作台-项目视图-项目情况-已投放未结清项目
  postProjectInfoPaynosettleList: (data) =>
    http.post('/dashboard/project/info/paynosettle/list', data),
  // 业务工作台-项目视图-项目情况-已投放未结清项目-导出
  postProjectInfoPaynosettleListExport: (data) =>
    http.post('/dashboard/project/info/paynosettle/list/export', data, { type: 'download' }),

  // 存在逾期项目明细
  postProjectInfoOverdueList: (data) => http.post('/dashboard/project/info/overdue/list', data),
  // 存在逾期项目明细-导出
  postProjectInfoOverdueListExport: (data) =>
    http.post('/dashboard/project/info/overdue/list/export', data, { type: 'download' }),

  // 本月应收租金明细
  postProjectInfoRentthismonthList: (data) =>
    http.post('/dashboard/project/info/rentthismonth/list', data),
  // 本月应收租金明细-导出
  postProjectInfoRentthismonthListExport: (data) =>
    http.post('/dashboard/project/info/rentthismonth/list/export', data, { type: 'download' }),
}
