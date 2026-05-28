export default {
  '/risk/riskStrategy/sourceCard/detail/:id?': [
    { key: '/risk/riskStrategy', title: '评分卡' },
    { key: '/risk/riskStrategy/sourceCard/detail/:id?', title: '详情' },
  ],
  '/risk/riskStrategy/indicatorManage/detail/:id?': [
    { key: '/risk/riskStrategy', title: '风控策略' },
    { key: '/risk/riskStrategy/indicatorManage/detail/:id?', title: '指标详情' },
  ],
  '/afterLease/checkPlan/createPlan/:id?': [
    { key: '/afterLease/checkPlan', title: '租后检查' },
    { key: '/afterLease/checkPlan/createPlan/:id?', title: '详情' },
  ],
  '/contract/list/change/:id?': [
    { key: '/contract/list', title: '合同管理' },
    { key: '/contract/list/change/:id?', title: '合同变更' },
  ],
  '/contract/list/settlement/:id?': [
    { key: '/contract/list', title: '合同管理' },
    { key: '/contract/list/settlement/:id?', title: '合同结清' },
  ],
  '/contract/list/createReceipt/:id?': [
    { key: '/contract/list', title: '合同管理' },
    { key: '/contract/list/createReceipt/:id?', title: '新增投放' },
  ],
  '/contract/list/startRent/:id?': [
    { key: '/contract/list', title: '合同管理' },
    { key: '/contract/list/startRent/:id?', title: '合同起租' },
  ],
  '/contract/list/marginRefund/:id?': [
    { key: '/contract/list', title: '合同管理' },
    { key: '/contract/list/marginRefund/:id?', title: '保证金退抵' },
  ],
  '/kpi/projectAllot/history/:id': [
    { key: '/kpi/projectAllot', title: '项目分配表' },
    { key: '/kpi/projectAllot/history/:id', title: '历史记录' },
  ],
  '/contract/list/detail/leaseLog/:id?': [
    { key: '/contract/list', title: '合同管理' },
    { key: '/contract/list/detail/:id?', title: '合同详情' },
    { key: '/contract/list/detail/leaseLog/:id', title: '租赁物历史版本' },
    // { key: '/contract/list/detail/leaseLog/diffInfo/:id?', title: '租赁物版本比较' },
  ],
  '/financial/payment/batchApproval/:id?': [
    { key: '/financial/payment', title: '还本付息' },
    { key: '/financial/payment/batchApproval/:id?', title: '自动还款' },
  ],
  '/financial/fund/change/:id?': [
    { key: '/financial/fund', title: '融资管理' },
    { key: '/financial/fund/change/:id?', title: '贷后变更' },
  ],
  '/financial/fund/effect/:id?': [
    { key: '/financial/fund', title: '融资管理' },
    { key: '/financial/fund/effect/:id?', title: '融资生效' },
  ],
  '/afterLease/checkPlan/commonTemplate/:id?': [
    { key: '/afterLease/checkPlan', title: '租后检查' },
    { key: '/afterLease/checkPlan/commonTemplate/:id?', title: '模板详情' },
  ],
  '/customer/maintain/applyPermission/:id?': [
    { key: '/customer/maintain', title: '客户列表' },
    { key: '/customer/maintain/applyPermission/:id?', title: '客户权限申请' },
  ],
  '/budget/pricing/ftpInterest/priceDetail/:id?': [
    { key: '/budget/pricing/ftpInterest', title: 'FTP计息' },
    { key: '/budget/pricing/ftpInterest/priceDetail/:id?', title: 'FTP价格表详情' },
  ],

  '/kpi/estimation/contract/detail/:id?': [
    { key: '/kpi/estimation', title: '项目绩效测算表' },
    { key: '/kpi/estimation/contract/detail/:id?', title: '合同维度' },
  ],
  '/kpi/estimation/projectManagerPrize/detail/:id?': [
    { key: '/kpi/estimation', title: '项目绩效测算表' },
    { key: '/kpi/estimation/projectManagerPrize/detail/:id?', title: '项目经理奖金计算' },
  ],
  '/kpi/estimation/projectManagerProfit/detail/:id?': [
    { key: '/kpi/estimation', title: '项目绩效测算表' },
    { key: '/kpi/estimation/projectManagerProfit/detail/:id?', title: '项目经理利润完成率' },
  ],
  '/kpi/estimation/departmentalPool/detail/:id?': [
    { key: '/kpi/estimation', title: '项目绩效测算表' },
    { key: '/kpi/estimation/departmentalPool/detail/:id?', title: '部门池' },
  ],
  '/customer/debtRat/detail/:id?': [
    { key: '/project/review', title: '项目评审' },
    { key: '/customer/debtRat/detail/:id', title: '债项详情' },
  ],
  '/customerView/detail/:id?': [
    { key: '/customerView', title: '客户统一视图' },
    { key: '/customerView/detail/:id?', title: '客户详情' },
  ],
  '/customer/maintain/singleViewRisk': [
    { key: '/customer/maintain', title: '客户管理' },
    { key: '/customer/maintain/detail/:id?', title: '详情' },
    { key: '/customer/maintain/singleViewRisk', title: '客户风险单一视图' },
  ],
  '/afterLease/checkPlan/singleViewRisk': [
    { key: '/afterLease/checkPlan', title: '租后检查' },
    { key: '/afterLease/checkPlan/commonTemplate/:id?', title: '模板详情' },
    { key: '/afterLease/checkPlan/singleViewRisk', title: '客户风险单一视图' },
  ],
  '/customerView/singeView': [
    { key: '/customerView', title: '客户统一视图' },
    { key: '/customerView/detail/:id?', title: '客户详情' },
    { key: '/customer/singeView', title: '企查查' },
  ],
  '/customerView/detail': [
    { key: '/customerView', title: '客户统一视图' },
    { key: '/customerView/detail/:id?', title: '客户详情' },
  ],
  '/financial/liquidity/accountBalanceDetail/:id?': [
    { key: '/financial/liquidity', title: '流动性管理' },
    { key: '/financial/liquidity/accountBalanceDetail/:id?', title: '账户余额表' },
  ],
  '/financial/liquidity/predictionParameters/:id?': [
    { key: '/financial/liquidity', title: '流动性管理' },
    { key: '/financial/liquidity/predictionParameters/:id?', title: '流动性预测参数' },
  ],
  '/customerMonitoring/detail/:id': [
    { key: '/customerMonitoring', title: '客户监控' },
    { key: '/customerMonitoring/detail/:id?', title: '客户监控详情' },
  ],
  '/budgetManagement/placementPlan/weekDetail/:id?': [
    { key: '/budgetManagement/placementPlan', title: '投放计划' },
    { key: '/budgetManagement/placementPlan/weekDetail/:id?', title: '周报详情' },
  ],
  '/budgetManagement/plan/profit/businessDetail/:id?': [
    { key: '/budgetManagement/plan/profit', title: '预算管理' },
    { key: '/budgetManagement/plan/profit/detail/:id?', title: '利润预算' },
    { key: '/budgetManagement/plan/profit/businessDetail/:id?', title: '利润预算明细' },
  ],
  '/archives/otherFilingMaterials/detail/:id?': [
    { key: '/archives/otherFilingMaterials', title: '其他资料归档' },
    { key: '/archives/otherFilingMaterials/detail/:id?', title: '发起归档' },
  ],
}
