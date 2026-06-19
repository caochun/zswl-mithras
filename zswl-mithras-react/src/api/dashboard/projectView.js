import { http } from '@zswl/admin'

export default {
  // 项目阶段统计
  postProjectStageStatistics: (data, functionCode = 'dashboardprojectstagestatistics') =>
    http.post('/dashboard/project/stage/statistics', data, {
      headers: {
        functionCode,
      },
    }),

  // 立项阶段明细
  postProjectStageProjestablishList: (data) =>
    http.post('/dashboard/project/stage/projestablish/list', data),

  // 评审阶段明细
  postProjectStageProjreviewList: (data) =>
    http.post('/dashboard/project/stage/projreview/list', data),

  // 评审结束未创建合同明细
  postProjectStageProjreviewNocontractList: (data) =>
    http.post('/dashboard/project/stage/projreview/nocontract/list', data),

  // 签约阶段明细
  postProjectStageContractList: (data) => http.post('/dashboard/project/stage/contract/list', data),

  // 付款阶段明细
  postProjectStagePreparepaymentList: (data) =>
    http.post('/dashboard/project/stage/preparepayment/list', data),

  // 投放阶段明细
  postProjectStagePaymentList: (data) => http.post('/dashboard/project/stage/payment/list', data),

  // 还款阶段明细
  postProjectStageRepaymentList: (data) =>
    http.post('/dashboard/project/stage/repayment/list', data),

  // 待出具合规意见明细
  postProjectStageProjreviewLegalreportList: (data) =>
    http.post('/dashboard/project/stage/projreview/legalreport/list', data),

  // 项目信息统计
  postProjectInfoStatistics: (data) => http.post('/dashboard/project/info/statistics', data, {}),

  // 业务工作台-项目视图-项目情况-统计
  postProjectInfoStatisticsRentInfoList: (data) =>
    http.post('/dashboard/project/info/statisticsRentInfoList', data),

  // 3个月内结清项目明细
  postProjectInfoSettleinthreemonthList: (data) =>
    http.post('/dashboard/project/info/settleinthreemonth/list', data),

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
