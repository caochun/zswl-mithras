import { http } from '@zswl/admin'

export default {
  postDashboardFileExport: (data, functionCode = 'dashboardFileExport') =>
    http.post('/file/export', data, {
      type: 'download',
      headers: {
        functionCode,
      },
    }),
}

// DASHBOARD_CLIENT_OVERVIEW_ALL("统一工作台-客户视图-客户一览-所有客户"),
// DASHBOARD_CLIENT_OVERVIEW_SURVIVAL("统一工作台-客户视图-客户一览-存续客户"),
// DASHBOARD_CLIENT_OVERVIEW_SETTLE_IN_THREE_MONTH("统一工作台-客户视图-客户一3个月内结清客户"),
// DASHBOARD_CLIENT_OVERVIEW_OVERDUE("统一工作台-客户视图-客户一览-逾期客户"),

// DASHBOARD_CLIENT_OVERVIEW_AFTER_LEASE("统一工作台-客户视图-客户一览-租后检查"),

// DASHBOARD_FUND_FINANCE_REPAY("统一工作台-融资视图-还本付息"),
// DASHBOARD_FUND_FINANCE_LOAN_INFO("统一工作台-融资视图-融资情况"),
// DASHBOARD_FUND_FINANCE_CREDIT("统一工作台-融资视图-授信情况"),
// DASHBOARD_FUND_FINANCE_BALANCE("统一工作台-融资视图-融资余额"),
// DASHBOARD_FUND_FINANCE_COST_FUNDS("统一工作台-融资视图-资金成本"),

// DASHBOARD_PROJECT_SETTLE_IN_THREE_MONTH("统一工作台-项目视图-项目信息-3个月内结清项目明细"),

// DASHBOARD_PROJECT_OVERDUE_LIST("统一工作台-项目视图-项目信息-存在逾期项目明细"),
// DASHBOARD_PROJECT_RENT_THIS_MONTH("统一工作台-项目视图-项目信息-本月应收租金明细"),
// DASHBOARD_PROJECT_PROJECT_PAY_NO_SETTLE("统一工作台-项目视图-项目信息-已投放未结清项目"),
// DASHBOARD_PROJECT_PROVISION("统一工作台-项目视图-项目信息-剩余本金与拨备"),
// DASHBOARD_PROJECT_PLEDGE("统一工作台-项目视图-项目信息-质押/监管情况"),

// DASHBOARD_PROJECT_STAGE_ESTABLISH("统一工作台-项目视图-项目阶段-立项"),
// DASHBOARD_PROJECT_STAGE_REVIEW("统一工作台-项目视图-项目阶段-评审阶段"),
// DASHBOARD_PROJECT_STAGE_REVIEW_NO_CONTRACT("统一工作台-项目视图-项目阶段-评审通过未创建合同"),
// DASHBOARD_PROJECT_STAGE_CONTRACT("统一工作台-项目视图-项目阶段-签约阶段"),
// DASHBOARD_PROJECT_STAGE_PAYMENT("统一工作台-项目视图-项目阶段-投放阶段"),
// DASHBOARD_PROJECT_STAGE_PREPARE_PAYMENT("统一工作台-项目视图-项目阶段-付款阶段"),
// DASHBOARD_PROJECT_STAGE_PREPARE_REPAYMENT("统一工作台-项目视图-项目阶段-还款阶段"),
// DASHBOARD_PROJECT_STAGE_REVIEW_NO_LEGAL_REPORT("统一工作台-项目视图-项目阶段-待出具合规意见"),
