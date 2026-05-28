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

  // 3个月内结清项目明细
  postProjectInfoSettleinthreemonthList: (data) =>
    http.post('/dashboard/project/info/settleinthreemonth/list', data),

  // 存在逾期项目明细
  postProjectInfoOverdueList: (data) => http.post('/dashboard/project/info/overdue/list', data),

  // 存在逾期项目明细
  postProjectInfoOverdueList: (data) => http.post('/dashboard/project/info/overdue/list', data),

  // 本月应收租金明细
  postProjectInfoRentthismonthList: (data) =>
    http.post('/dashboard/project/info/rentthismonth/list', data),
}
