import { http } from '@zswl/admin'

export default {
  // 业务工作台-融资视图-分组统计
  postDashboardFinanceStatisticsList: (data) =>
    http.post('/dashboard/finance/statisticsList', data),

  // 业务工作台-融资视图-融资信息
  postDashboardFinanceLoanIfoList: (data) => http.post('/dashboard/finance/loaninfo/list', data),

  // 业务工作台-融资视图-融资余额
  postDashboardFinanceBalanceList: (data) => http.post('/dashboard/finance/balance/list', data),

  // 业务工作台-融资视图-还本付息
  postDashboardFinanceRepayList: (data) => http.post('/dashboard/finance/repay/list', data),

  // 业务工作台-融资视图-授信情况
  postDashboardFinanceCreditinfoList: (data) =>
    http.post('/dashboard/finance/creditinfo/list', data),

  // 业务工作台-融资视图-资金成本
  postDashboardFinanceFundsList: (data) => http.post('/dashboard/finance/funds/list', data),
}
