const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const readmePath = path.join(root, 'README.md')
const { findUnusedComponentCandidates } = require('./report-unused-component-candidates')
const { analyzeUiDomainDeps } = require('./report-ui-domain-deps')
const {
  analyzeComponentEntryDeps,
  findStaleBaselineEdges,
  findUnlistedEdges,
} = require('./report-component-entry-deps')
const { createDomainAliases } = require('./domain-report-config')
const scanDirs = [srcDir]
const componentEntryDepsBaselinePath = path.join(
  __dirname,
  'component-entry-deps-baseline.json'
)
const allFilePattern = /./
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const scannableFilePattern = /\.(js|jsx|ts|tsx|less)$/
const styleFilePattern = /\.(less|css|scss|sass)$/
const copiedSourceFilePattern =
  /(?:^|[\\/])(?:copy|backup|bak)[\\/]|(?:^|[\\/])[^\\/]*(?: copy|副本|备份|backup|bak)\.(?:js|jsx|ts|tsx)$/i
const sampleSourceFilePattern =
  /(?:^|[\\/])(?:mock|demo|example)[\\/]|(?:^|[\\/])(?:mock|demo|example)\.(?:js|jsx|ts|tsx)$/i
const sourceExtensions = ['.js', '.jsx', '.ts', '.tsx']
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g
const styleImportPattern = /@import\s+(?:\([^)]*\)\s*)?['"]~?([^'"]+)['"]/g
const componentApiForwardingShellPattern =
  /^export\s+\{\s*default\s*\}\s+from\s+['"]@\/api\/[^'"]+['"]\s*;?\s*$/
const componentForwardingShellPattern =
  /^export\s+\{\s*default\s*\}\s+from\s+['"]@\/components\/[^'"]+['"]\s*;?\s*$/
const pageRouteShellPattern =
  /^export\s+\{[\s\S]*\}\s+from\s+['"]@\/components\/[^/'"]+\/[^/'"]*(?:Entries|entries)(?:\.js)?['"]\s*;?\s*$/
const uiLocalApiFilePattern =
  /^src[\\/](?:components|pages|layout)[\\/].*[\\/]api\.(?:js|jsx|ts|tsx)$/
const rootApiFilePattern = /^src[\\/]api[\\/][^\\/]+\.(?:js|jsx|ts|tsx)$/
const relativeApiImportPattern =
  /^\.{1,2}(?:\/[^'"]*)?\/api(?:\.(?:js|jsx|ts|tsx)|\/index(?:\.(?:js|jsx|ts|tsx))?)?$/
const utilitySourceFilePattern = /^src[\\/]utils[\\/].*\.(?:js|jsx|ts|tsx)$/
const removedLegacyUtilityFiles = new Map([
  ['src/utils/afterLease.js', 'src/utils/domains/afterLease/AfterLeaseUtils.js'],
  ['src/utils/budgetManagement.js', 'src/utils/domains/budget/ProvisionForecastUtils.js'],
  [
    'src/utils/domains/budgetManagement/BudgetManagementUtils.js',
    'src/utils/domains/budget/ProvisionForecastUtils.js',
  ],
  ['src/utils/customer.js', 'src/utils/domains/customer/CustomerUtils.js'],
  ['src/utils/customerRat.js', 'src/utils/domains/customer/CustomerRatUtils.js'],
  ['src/utils/dashboard.js', 'src/utils/domains/dashboard/DashboardUtils*.js'],
  ['src/utils/dashboardColumns.js', 'src/utils/domains/dashboard/DashboardUtilsColumns.js'],
  ['src/utils/dashboardFilterKeys.js', 'src/utils/domains/dashboard/DashboardUtilsFilterKeys.js'],
  ['src/utils/dashboardOperation.js', 'src/utils/domains/dashboard/DashboardUtilsOperation.js'],
  ['src/utils/kpi.js', 'src/utils/domains/kpi/KpiUtils.js'],
  ['src/utils/paymentApplication.js', 'src/utils/domains/cpm/PaymentApplicationUtils.js'],
  ['src/utils/processFlow.js', 'src/utils/domains/process/ProcessFlowContext.js'],
  ['src/utils/report.js', 'src/utils/domains/report/ReportUtils.js'],
  ['src/utils/risk.js', 'src/utils/domains/risk/RiskUtils.js'],
  ['src/utils/rzyConfig.js', 'src/utils/domains/rzy/RzyConfig.js'],
  ['src/utils/options/financialReport.js', 'src/utils/domains/report/* when report options are needed'],
  ['src/utils/options/ftp.js', 'src/utils/domains/budget/* when FTP pricing options are needed'],
  ['src/utils/hooks/useGetColumns.js', 'domain component-local options helpers when needed'],
  ['src/utils/hooks/useGetStatus.js', 'src/utils/domains/blackGray/BlackGrayStatusUtils.js'],
  ['src/utils/hooks/useLayoutEffect.js', 'React.useLayoutEffect or domain-local hooks when needed'],
])
const removedLegacyComponentFiles = new Map([
  [
    'src/components/AfterLease/Adjust/List/CreateModal/index.js',
    'src/components/AfterLease/Adjust/List/CreateModal/AfterLeaseAdjustCreateModal.js',
  ],
  [
    'src/components/AfterLease/Adjust/List/index.js',
    'src/components/AfterLease/Adjust/List/AfterLeaseAdjustList.js',
  ],
  [
    'src/components/AfterLease/CheckPlan/List/Tab/CheckList/index.js',
    'src/components/AfterLease/CheckPlan/List/Tab/CheckList/AfterLeaseCheckPlanCheckList.js',
  ],
  [
    'src/components/AfterLease/CheckPlan/List/Tab/CheckList/CreateModal/index.js',
    'src/components/AfterLease/CheckPlan/List/Tab/CheckList/CreateModal/AfterLeaseCheckPlanCheckListCreateModal.js',
  ],
  [
    'src/components/AfterLease/CheckPlan/List/Tab/OpenList/index.js',
    'src/components/AfterLease/CheckPlan/List/Tab/OpenList/AfterLeaseCheckPlanOpenList.js',
  ],
  [
    'src/components/AfterLease/CheckPlan/List/Tab/Strategy/CreateModal/index.js',
    'src/components/AfterLease/CheckPlan/List/Tab/Strategy/CreateModal/AfterLeaseCheckPlanStrategyCreateModal.js',
  ],
  [
    'src/components/AfterLease/CheckPlan/List/Tab/Strategy/index.js',
    'src/components/AfterLease/CheckPlan/List/Tab/Strategy/AfterLeaseCheckPlanStrategyPage.js',
  ],
  [
    'src/components/AfterLease/CheckPlanPrepareProcess/index.js',
    'src/components/AfterLease/CheckPlanPrepareProcess/AfterLeaseCheckPlanPrepareProcess.js',
  ],
  [
    'src/components/AfterLease/ManageLedger/index.js',
    'src/components/AfterLease/ManageLedger/AfterLeaseManageLedger.js',
  ],
  [
    'src/components/AfterLease/PolicyManage/AddModal/index.js',
    'src/components/AfterLease/PolicyManage/AddModal/AfterLeasePolicyManageAddModal.js',
  ],
  [
    'src/components/AfterLease/PolicyManage/Base/Policy/FilesManageDraw/index.js',
    'src/components/AfterLease/PolicyManage/Base/Policy/FilesManageDraw/AfterLeasePolicyManagePolicyFilesManageDraw.js',
  ],
  [
    'src/components/AfterLease/PolicyManage/Base/Policy/index.js',
    'src/components/AfterLease/PolicyManage/Base/Policy/AfterLeasePolicyManagePolicy.js',
  ],
  [
    'src/components/AfterLease/PolicyManage/Base/Policy/PolicyModal/index.js',
    'src/components/AfterLease/PolicyManage/Base/Policy/PolicyModal/AfterLeasePolicyManagePolicyModal.js',
  ],
  [
    'src/components/AfterLease/PolicyManageRemind/index.js',
    'src/components/AfterLease/PolicyManageRemind/AfterLeasePolicyManageRemind.js',
  ],
  [
    'src/components/AfterLease/RentCollection/List/index.js',
    'src/components/AfterLease/RentCollection/List/AfterLeaseRentCollectionList.js',
  ],
  [
    'src/components/AfterLease/RentCollection/ListRender/index.js',
    'src/components/AfterLease/RentCollection/ListRender/RentCollectionListRender.js',
  ],
  [
    'src/components/AfterLease/RentCollection/BatchInterest/index.js',
    'src/components/AfterLease/RentCollection/BatchInterest/AfterLeaseRentCollectionBatchInterest.js',
  ],
  [
    'src/components/Archives/Management/detail/index.js',
    'src/components/Archives/Management/detail/ArchivesManagementDetail.js',
  ],
  [
    'src/components/Archives/OtherFilingMaterials/detail/index.js',
    'src/components/Archives/OtherFilingMaterials/detail/ArchivesOtherFilingMaterialsDetail.js',
  ],
  [
    'src/components/App/RootRedirect/index.js',
    'src/components/App/RootRedirect/RootRedirect.js',
  ],
  [
    'src/components/BaseData/LeaseholdProperty/index.js',
    'src/components/BaseData/LeaseholdProperty/BaseDataLeaseholdProperty.js',
  ],
  [
    'src/components/BaseData/FileTemplate/index.js',
    'src/components/BaseData/FileTemplate/BaseDataFileTemplate.js',
  ],
  [
    'src/components/Chart/BarChart/index.js',
    'src/components/Chart/BarChart/BarChart.js',
  ],
  [
    'src/components/Chart/LineChart/index.js',
    'src/components/Chart/LineChart/LineChart.js',
  ],
  [
    'src/components/BlackGray/Info/index.js',
    'src/components/BlackGray/Info/BlackGrayHitInfo.js',
  ],
  [
    'src/components/BlackGray/BreakThrough/Application/index.js',
    'src/components/BlackGray/BreakThrough/Application/BlackGrayBreakThroughApplication.js',
  ],
  [
    'src/components/BlackGray/EnterDatabase/Application/index.js',
    'src/components/BlackGray/EnterDatabase/Application/BlackGrayEnterDatabaseApplication.js',
  ],
  [
    'src/components/BlackGray/EnterDatabase/External/index.js',
    'src/components/BlackGray/EnterDatabase/External/BlackGrayEnterDatabaseExternal.js',
  ],
  [
    'src/components/BlackGray/EnterDatabase/Upload/index.js',
    'src/components/BlackGray/EnterDatabase/Upload/BlackGrayEnterDatabaseUpload.js',
  ],
  [
    'src/components/BlackGray/Outbound/Application/index.js',
    'src/components/BlackGray/Outbound/Application/BlackGrayOutboundApplication.js',
  ],
  [
    'src/components/BlackGray/Outbound/Search/index.js',
    'src/components/BlackGray/Outbound/Search/BlackGrayOutboundSearch.js',
  ],
  [
    'src/components/BlackGray/Warehouse/Search/index.js',
    'src/components/BlackGray/Warehouse/Search/BlackGrayWarehouseSearch.js',
  ],
  [
    'src/components/BlackGray/Warehouse/Rule/index.js',
    'src/components/BlackGray/Warehouse/Rule/BlackGrayWarehouseRule.js',
  ],
  [
    'src/components/BlackGray/Warehouse/SubTask/index.js',
    'src/components/BlackGray/Warehouse/SubTask/BlackGrayWarehouseSubTask.js',
  ],
  [
    'src/components/Budget/FinancingCostEditModal/index.js',
    'src/components/Budget/FinancingCostEditModal/BudgetFinancingCostEditModal.js',
  ],
  [
    'src/components/Budget/FtpInterestPriceChangeModal/index.js',
    'src/components/Budget/FtpInterestPriceChangeModal/BudgetFtpInterestPriceChangeModal.js',
  ],
  [
    'src/components/Budget/IncomeShareTable/index.js',
    'src/components/Budget/IncomeShareTable/BudgetIncomeShareTable.js',
  ],
  [
    'src/components/Budget/IncomeShareTableDetail/index.js',
    'src/components/Budget/IncomeShareTableDetail/BudgetIncomeShareTableDetail.js',
  ],
  [
    'src/components/Budget/Lpr/index.js',
    'src/components/Budget/Lpr/BudgetLpr.js',
  ],
  [
    'src/components/Budget/PricingBaseSet/List/index.js',
    'src/components/Budget/PricingBaseSet/List/BudgetPricingBaseSetList.js',
  ],
  [
    'src/components/Budget/PricingBaseSetModalDetail/index.js',
    'src/components/Budget/PricingBaseSetModalDetail/BudgetPricingBaseSetModalDetail.js',
  ],
  [
    'src/components/Budget/PricingBaseSetModalDetail/ModalEditTable/index.js',
    'src/components/Budget/PricingBaseSetModalDetail/ModalEditTable/BudgetPricingBaseSetModalEditTable.js',
  ],
  [
    'src/components/Budget/PricingBusiness/List/index.js',
    'src/components/Budget/PricingBusiness/List/BudgetPricingBusinessList.js',
  ],
  [
    'src/components/Budget/PricingBusinessDetail/Log/index.js',
    'src/components/Budget/PricingBusinessDetail/Log/BudgetPricingBusinessDetailLog.js',
  ],
  [
    'src/components/Budget/PricingFtpInterest/List/index.js',
    'src/components/Budget/PricingFtpInterest/List/BudgetPricingFtpInterestList.js',
  ],
  [
    'src/components/Budget/PricingFtpInterest/Detail/index.js',
    'src/components/Budget/PricingFtpInterest/Detail/BudgetPricingFtpInterestDetail.js',
  ],
  [
    'src/components/Budget/PricingFtpYield/List/index.js',
    'src/components/Budget/PricingFtpYield/List/BudgetPricingFtpYieldList.js',
  ],
  [
    'src/components/Budget/PricingFtpYield/Detail/index.js',
    'src/components/Budget/PricingFtpYield/Detail/BudgetPricingFtpYieldDetail.js',
  ],
  [
    'src/components/Budget/ProvisioningDataAddModal/index.js',
    'src/components/Budget/ProvisioningDataAddModal/BudgetProvisioningDataAddModal.js',
  ],
  [
    'src/components/Budget/ProvisioningDataSearch/List/index.js',
    'src/components/Budget/ProvisioningDataSearch/List/BudgetProvisioningDataSearchList.js',
  ],
  [
    'src/components/Budget/ProvisioningImpairment/Detail/index.js',
    'src/components/Budget/ProvisioningImpairment/Detail/BudgetProvisioningImpairmentDetail.js',
  ],
  [
    'src/components/Budget/ProvisioningImpairment/List/index.js',
    'src/components/Budget/ProvisioningImpairment/List/BudgetProvisioningImpairmentList.js',
  ],
  [
    'src/components/Budget/ProvisioningImpairmentColumns/index.js',
    'src/components/Budget/ProvisioningImpairmentColumns/BudgetProvisioningImpairmentColumns.js',
  ],
  [
    'src/components/Budget/ProvisioningParamsConfig/Detail/index.js',
    'src/components/Budget/ProvisioningParamsConfig/Detail/BudgetProvisioningParamsConfigDetail.js',
  ],
  [
    'src/components/Budget/ProvisioningParamsConfig/List/index.js',
    'src/components/Budget/ProvisioningParamsConfig/List/BudgetProvisioningParamsConfigList.js',
  ],
  [
    'src/components/Budget/ProvisioningParamsConfigColumns/index.js',
    'src/components/Budget/ProvisioningParamsConfigColumns/BudgetProvisioningParamsConfigColumns.js',
  ],
  [
    'src/components/Budget/AccountsReceivable/index.js',
    'src/components/Budget/AccountsReceivable/BudgetAccountsReceivable.js',
  ],
  [
    'src/components/Budget/AccountsReceivableDetail/index.js',
    'src/components/Budget/AccountsReceivableDetail/BudgetAccountsReceivableDetail.js',
  ],
  [
    'src/components/Budget/BankAccount/index.js',
    'src/components/Budget/BankAccount/BudgetBankAccount.js',
  ],
  [
    'src/components/Budget/ExchangeRate/index.js',
    'src/components/Budget/ExchangeRate/BudgetExchangeRate.js',
  ],
  [
    'src/components/Budget/PricingFtpInterest/PriceDetail/index.js',
    'src/components/Budget/PricingFtpInterest/PriceDetail/BudgetPricingFtpInterestPriceDetail.js',
  ],
  [
    'src/components/BudgetManagement/PlanCost/Detail/index.js',
    'src/components/BudgetManagement/PlanCost/Detail/BudgetManagementPlanCostDetail.js',
  ],
  [
    'src/components/BudgetManagement/PlanCost/List/index.js',
    'src/components/BudgetManagement/PlanCost/List/BudgetManagementPlanCostList.js',
  ],
  [
    'src/components/BudgetManagement/PlanProfit/BusinessDetail/index.js',
    'src/components/BudgetManagement/PlanProfit/BusinessDetail/BudgetManagementPlanProfitBusinessDetail.js',
  ],
  [
    'src/components/BudgetManagement/PlanProfit/List/index.js',
    'src/components/BudgetManagement/PlanProfit/List/BudgetManagementPlanProfitList.js',
  ],
  [
    'src/components/BudgetManagement/Assessment/index.js',
    'src/components/BudgetManagement/Assessment/BudgetManagementAssessment.js',
  ],
  [
    'src/components/BudgetManagement/ProvisionForecast/Detail/index.js',
    'src/components/BudgetManagement/ProvisionForecast/Detail/BudgetManagementProvisionForecastDetail.js',
  ],
  [
    'src/components/BudgetManagement/ProvisionForecast/ConfigDetail/index.js',
    'src/components/BudgetManagement/ProvisionForecast/ConfigDetail/BudgetManagementProvisionForecastConfigDetail.js',
  ],
  [
    'src/components/BudgetManagement/ProvisionForecast/List/index.js',
    'src/components/BudgetManagement/ProvisionForecast/List/BudgetManagementProvisionForecastList.js',
  ],
  [
    'src/components/Archives/Manage/detail/index.js',
    'src/components/Archives/Manage/detail/ArchivesManageDetail.js',
  ],
  [
    'src/components/Archives/Task/index.js',
    'src/components/Archives/Task/ArchivesTask.js',
  ],
  [
    'src/components/BlackGray/BreakThrough/Approval/index.js',
    'src/components/BlackGray/BreakThrough/Approval/BlackGrayBreakThroughApproval.js',
  ],
  [
    'src/components/BlackGray/EnterDatabase/History/index.js',
    'src/components/BlackGray/EnterDatabase/History/BlackGrayEnterDatabaseHistory.js',
  ],
  [
    'src/components/BlackGray/Outbound/Approval/index.js',
    'src/components/BlackGray/Outbound/Approval/BlackGrayOutboundApproval.js',
  ],
  [
    'src/components/BusinessInfoCheck/index.js',
    'src/components/BusinessInfoCheck/BusinessInfoCheck.js',
  ],
  [
    'src/components/ClientMaterialTable/index.js',
    'src/components/ClientMaterialTable/BusinessMaterialTable.js',
  ],
  [
    'src/components/Contract/ApplicationDetail/LeaseLog/index.js',
    'src/components/Contract/ApplicationDetail/LeaseLog/ContractApplicationLeaseLog.js',
  ],
  [
    'src/components/Contract/ContractMaterials/index.js',
    'src/components/Contract/ContractMaterials/ContractMaterials.js',
  ],
  [
    'src/components/Contract/ContractText/index.js',
    'src/components/Contract/ContractText/ContractText.js',
  ],
  [
    'src/components/Contract/DepositRefundNotification/index.js',
    'src/components/Contract/DepositRefundNotification/ContractDepositRefundNotification.js',
  ],
  [
    'src/components/Contract/ChangeMaterials/index.js',
    'src/components/Contract/ChangeMaterials/ContractChangeMaterials.js',
  ],
  [
    'src/components/Contract/LeaseMaterials/index.js',
    'src/components/Contract/LeaseMaterials/ContractLeaseMaterials.js',
  ],
  [
    'src/components/Contract/ChangeProtocol/index.js',
    'src/components/Contract/ChangeProtocol/ContractChangeProtocol.js',
  ],
  [
    'src/components/Contract/SettlementProtocol/index.js',
    'src/components/Contract/SettlementProtocol/ContractSettlementProtocol.js',
  ],
  [
    'src/components/Contract/StartRentMaterials/index.js',
    'src/components/Contract/StartRentMaterials/ContractStartRentMaterials.js',
  ],
  [
    'src/components/Contract/StartRentDetail/index.js',
    'src/components/Contract/StartRentDetail/ContractStartRentDetail.js',
  ],
  [
    'src/components/Contract/CreateReceiptDetail/index.js',
    'src/components/Contract/CreateReceiptDetail/ContractCreateReceiptDetail.js',
  ],
  [
    'src/components/Customer/FinancialReport/DeteleIcon.js',
    'src/components/Customer/FinancialReport/DeleteIcon.js',
  ],
  [
    'src/components/Customer/QccSingleView/index.js',
    'src/components/Customer/QccSingleView/CustomerQccSingleView.js',
  ],
  [
    'src/components/Customer/SingleViewRisk/index.js',
    'src/components/Customer/SingleViewRisk/CustomerSingleViewRisk.js',
  ],
  [
    'src/components/Customer/UnifiedView/index.js',
    'src/components/Customer/UnifiedView/CustomerUnifiedView.js',
  ],
  [
    'src/components/Customer/CustomerRatColumns/index.js',
    'src/components/Customer/CustomerRatColumns/CustomerRatingColumns.js',
  ],
  [
    'src/components/Customer/DebtRat/index.js',
    'src/components/Customer/DebtRat/CustomerDebtRat.js',
  ],
  [
    'src/components/Customer/ApplyPermission/index.js',
    'src/components/Customer/ApplyPermission/CustomerApplyPermission.js',
  ],
  [
    'src/components/Credit/CreditSearchModal/index.js',
    'src/components/Credit/CreditSearchModal/CreditReportSearchModal.js',
  ],
  [
    'src/components/Credit/Establish/index.js',
    'src/components/Credit/Establish/CreditEstablish.js',
  ],
  [
    'src/components/Credit/CreditSearchList/index.js',
    'src/components/Credit/CreditSearchList/CreditSearchList.js',
  ],
  [
    'src/components/Credit/EstablishDetail/Log/DiffInfo/index.js',
    'src/components/Credit/EstablishDetail/Log/DiffInfo/CreditEstablishLogDiff.js',
  ],
  [
    'src/components/Credit/EstablishDetail/Log/index.js',
    'src/components/Credit/EstablishDetail/Log/CreditEstablishDetailLog.js',
  ],
  [
    'src/components/Credit/Review/index.js',
    'src/components/Credit/Review/CreditReviewPage.js',
  ],
  [
    'src/components/Credit/ReviewDetail/Log/DiffInfo/index.js',
    'src/components/Credit/ReviewDetail/Log/DiffInfo/CreditReviewLogDiff.js',
  ],
  [
    'src/components/Credit/ReviewDetail/Log/index.js',
    'src/components/Credit/ReviewDetail/Log/CreditReviewDetailLog.js',
  ],
  [
    'src/components/CreditManage/CreditTable/FinishView/Account/index.js',
    'src/components/CreditManage/CreditTable/FinishView/Account/CreditTableFinishAccount.js',
  ],
  [
    'src/components/CreditManage/CreditTable/FinishView/Batch/index.js',
    'src/components/CreditManage/CreditTable/FinishView/Batch/CreditTableFinishBatch.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Finish/index.js',
    'src/components/CreditManage/CreditTable/Finish/CreditTableFinish.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/BaoZheng/index.js',
    'src/components/CreditManage/CreditTable/Tab/BaoZheng/CreditTableBaoZheng.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/DiYa/index.js',
    'src/components/CreditManage/CreditTable/Tab/DiYa/CreditTableDiYa.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/HuanKuan/index.js',
    'src/components/CreditManage/CreditTable/Tab/HuanKuan/CreditTableHuanKuan.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/JiaoYi/index.js',
    'src/components/CreditManage/CreditTable/Tab/JiaoYi/CreditTableJiaoYi.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/KuHu/index.js',
    'src/components/CreditManage/CreditTable/Tab/KuHu/CreditTableKuHu.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/Level5/index.js',
    'src/components/CreditManage/CreditTable/Tab/Level5/CreditTableLevel5.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/Level5/CreateModal/index.js',
    'src/components/CreditManage/CreditTable/Tab/Level5/CreateModal/CreditTableLevel5CreateModal.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/YuQi/index.js',
    'src/components/CreditManage/CreditTable/Tab/YuQi/CreditTableYuQi.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/ZhangHu/index.js',
    'src/components/CreditManage/CreditTable/Tab/ZhangHu/CreditTableZhangHu.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Tab/ZhiYa/index.js',
    'src/components/CreditManage/CreditTable/Tab/ZhiYa/CreditTableZhiYa.js',
  ],
  [
    'src/components/CreditManage/CreditTable/Wait/index.js',
    'src/components/CreditManage/CreditTable/Wait/CreditTableWait.js',
  ],
  [
    'src/components/CreditManage/CreditTableConfig/index.js',
    'src/components/CreditManage/CreditTableConfig/CreditTableConfig.js',
  ],
  [
    'src/components/Cpm/BillManage/index.js',
    'src/components/Cpm/BillManage/CpmBillManage.js',
  ],
  [
    'src/components/Cpm/ContractCpm/CashFlowTable/index.js',
    'src/components/Cpm/ContractCpm/CashFlowTable/CpmContractCpmCashFlowTable.js',
  ],
  [
    'src/components/Cpm/ContractCpm/ContractDownPayment/index.js',
    'src/components/Cpm/ContractCpm/ContractDownPayment/CpmContractDownPayment.js',
  ],
  [
    'src/components/Cpm/MarginManagement/PaymentRecords/index.js',
    'src/components/Cpm/MarginManagement/PaymentRecords/CpmMarginPaymentRecords.js',
  ],
  [
    'src/components/Cpm/MarginManagement/RefundRecords/index.js',
    'src/components/Cpm/MarginManagement/RefundRecords/CpmMarginManagementRefundRecords.js',
  ],
  [
    'src/components/Cpm/MarginManagement/VerificationRecords/index.js',
    'src/components/Cpm/MarginManagement/VerificationRecords/CpmMarginManagementVerificationRecords.js',
  ],
  [
    'src/components/Cpm/PaymentApplication/PublicInformation/index.js',
    'src/components/Cpm/PaymentApplication/PublicInformation/CpmPaymentApplicationPublicInformation.js',
  ],
  [
    'src/components/Cpm/PaymentApplication/PublicCheckModal/index.js',
    'src/components/Cpm/PaymentApplication/PublicCheckModal/CpmPaymentApplicationPublicCheckModal.js',
  ],
  [
    'src/components/Cpm/PaymentApplicationList/index.js',
    'src/components/Cpm/PaymentApplicationList/CpmPaymentApplicationList.js',
  ],
  [
    'src/components/Cpm/PaymentApplicationList/AddModal/index.js',
    'src/components/Cpm/PaymentApplicationList/AddModal/CpmPaymentApplicationAddModal.js',
  ],
  [
    'src/components/Cpm/PaymentWriteOff/index.js',
    'src/components/Cpm/PaymentWriteOff/CpmPaymentWriteOff.js',
  ],
  [
    'src/components/Dashboard/Sso/index.js',
    'src/components/Dashboard/Sso/DashboardSso.js',
  ],
  [
    'src/components/Dashboard/SsoFlow/index.js',
    'src/components/Dashboard/SsoFlow/DashboardSsoFlow.js',
  ],
  [
    'src/components/EvaluationAgency/index.js',
    'src/components/EvaluationAgency/AppraisalAgency.js',
  ],
  [
    'src/components/ExternalEmbed/RzyIframe/index.js',
    'src/components/ExternalEmbed/RzyIframe/RzyIframe.js',
  ],
  [
    'src/components/ExternalEmbed/RzyPages/index.js',
    'src/components/ExternalEmbed/RzyPages/RzyPages.js',
  ],
  [
    'src/components/FilingMaterials/AfterFilingMaterialsApply/index.js',
    'src/components/FilingMaterials/AfterFilingMaterialsApply/FilingMaterialsAfterApply.js',
  ],
  [
    'src/components/FilingMaterials/FundFilingMaterialsApply/index.js',
    'src/components/FilingMaterials/FundFilingMaterialsApply/FilingMaterialsFundApply.js',
  ],
  [
    'src/components/FilingMaterials/OtherFilingMaterialsApply/index.js',
    'src/components/FilingMaterials/OtherFilingMaterialsApply/FilingMaterialsOtherApply.js',
  ],
  [
    'src/components/Financial/FundList/ChangeModal/index.js',
    'src/components/Financial/FundList/ChangeModal/FinancialFundListChangeModal.js',
  ],
  [
    'src/components/Financial/FundList/CreateModal/index.js',
    'src/components/Financial/FundList/CreateModal/FinancialFundListCreateModal.js',
  ],
  [
    'src/components/Financial/FundList/index.js',
    'src/components/Financial/FundList/FinancialFundList.js',
  ],
  [
    'src/components/Financial/FundDetail/ActualTable/index.js',
    'src/components/Financial/FundDetail/ActualTable/FinancialFundDetailActualTable.js',
  ],
  [
    'src/components/Financial/FundActualTable/index.js',
    'src/components/Financial/FundActualTable/FinancialFundActualTable.js',
  ],
  [
    'src/components/Financial/FundChangeLog/index.js',
    'src/components/Financial/FundChangeLog/FinancialFundChangeLog.js',
  ],
  [
    'src/components/Financial/FundDetail/EstimateTable/index.js',
    'src/components/Financial/FundDetail/EstimateTable/FinancialFundDetailEstimateTable.js',
  ],
  [
    'src/components/Financial/FundDetail/OtherAccount/index.js',
    'src/components/Financial/FundDetail/OtherAccount/FinancialFundDetailOtherAccount.js',
  ],
  [
    'src/components/Financial/FundDetail/RefundAccount/index.js',
    'src/components/Financial/FundDetail/RefundAccount/FinancialFundDetailRefundAccount.js',
  ],
  [
    'src/components/Financial/FundDetail/Scheme/index.js',
    'src/components/Financial/FundDetail/Scheme/FinancialFundDetailScheme.js',
  ],
  [
    'src/components/Financial/FundForm/FormGuarantee/index.js',
    'src/components/Financial/FundForm/FormGuarantee/FinancialFundFormGuarantee.js',
  ],
  [
    'src/components/Financial/FundForm/FormOrg/index.js',
    'src/components/Financial/FundForm/FormOrg/FinancialFundFormOrg.js',
  ],
  [
    'src/components/Financial/FundGuaranteeScheme/index.js',
    'src/components/Financial/FundGuaranteeScheme/FinancialFundGuaranteeScheme.js',
  ],
  [
    'src/components/Financial/FundYearRate/index.js',
    'src/components/Financial/FundYearRate/FinancialFundYearRate.js',
  ],
  [
    'src/components/Financial/Liquidity/FundDailyReport/index.js',
    'src/components/Financial/Liquidity/FundDailyReport/FinancialLiquidityFundDailyReport.js',
  ],
  [
    'src/components/Financial/PaymentList/index.js',
    'src/components/Financial/PaymentList/FinancialPaymentList.js',
  ],
  [
    'src/components/Financial/PaymentBatchApproval/index.js',
    'src/components/Financial/PaymentBatchApproval/FinancialPaymentBatchApproval.js',
  ],
  [
    'src/components/Financial/PaymentChangeLog/index.js',
    'src/components/Financial/PaymentChangeLog/FinancialPaymentChangeLog.js',
  ],
  [
    'src/components/Financial/FinancingCarryInterestFlow/index.js',
    'src/components/Financial/FinancingCarryInterestFlow/FinancialFinancingCarryInterestFlow.js',
  ],
  [
    'src/components/Financial/Org/index.js',
    'src/components/Financial/Org/FinancialOrg.js',
  ],
  [
    'src/components/Financial/Property/index.js',
    'src/components/Financial/Property/FinancialProperty.js',
  ],
  [
    'src/components/InsurancePolicy/Columns/index.js',
    'src/components/InsurancePolicy/Columns/InsurancePolicyColumns.js',
  ],
  [
    'src/components/InsurancePolicy/index.js',
    'src/components/InsurancePolicy/InsurancePolicy.js',
  ],
  [
    'src/components/Kpi/BaseSet/CopyItemModal/index.js',
    'src/components/Kpi/BaseSet/CopyItemModal/KpiBaseSetCopyItemModal.js',
  ],
  [
    'src/components/Kpi/BaseSet/ParameterModal/BasePrizeRate/index.js',
    'src/components/Kpi/BaseSet/ParameterModal/BasePrizeRate/KpiBaseSetBasePrizeRate.js',
  ],
  [
    'src/components/Kpi/BaseSet/ParameterModal/ProjectScaleFactor/index.js',
    'src/components/Kpi/BaseSet/ParameterModal/ProjectScaleFactor/KpiBaseSetProjectScaleFactor.js',
  ],
  [
    'src/components/Kpi/BaseSet/ParameterModal/ProjectTypeFactor/index.js',
    'src/components/Kpi/BaseSet/ParameterModal/ProjectTypeFactor/KpiBaseSetProjectTypeFactor.js',
  ],
  [
    'src/components/Kpi/BaseSet/ParameterModal/PutPrizeFactor/index.js',
    'src/components/Kpi/BaseSet/ParameterModal/PutPrizeFactor/KpiBaseSetPutPrizeFactor.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/BaBeiJiTi/index.js',
    'src/components/Kpi/BaseSetModalDetail/BaBeiJiTi/KpiBaseSetBaBeiJiTi.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/BuMenLiLun/index.js',
    'src/components/Kpi/BaseSetModalDetail/BuMenLiLun/KpiBaseSetBuMenLiLun.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/GongShiLiLun/index.js',
    'src/components/Kpi/BaseSetModalDetail/GongShiLiLun/KpiBaseSetGongShiLiLun.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/FeiYongJiTi/index.js',
    'src/components/Kpi/BaseSetModalDetail/FeiYongJiTi/KpiBaseSetExpenseAccrual.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/JinRongShiChangDept/index.js',
    'src/components/Kpi/BaseSetModalDetail/JinRongShiChangDept/KpiBaseSetJinRongShiChangDept.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/JinRongShiChangTiJiang/index.js',
    'src/components/Kpi/BaseSetModalDetail/JinRongShiChangTiJiang/KpiBaseSetJinRongShiChangTiJiang.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/SuiLvWeiHu/index.js',
    'src/components/Kpi/BaseSetModalDetail/SuiLvWeiHu/KpiBaseSetSuiLvWeiHu.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/XiangMuTiJiang/index.js',
    'src/components/Kpi/BaseSetModalDetail/XiangMuTiJiang/KpiBaseSetXiangMuTiJiang.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/YeWuDept/index.js',
    'src/components/Kpi/BaseSetModalDetail/YeWuDept/KpiBaseSetYeWuDept.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/ZhiDengXiShu/index.js',
    'src/components/Kpi/BaseSetModalDetail/ZhiDengXiShu/KpiBaseSetZhiDengXiShu.js',
  ],
  [
    'src/components/Kpi/BaseSetModalDetail/ZhongHouTaiDept/index.js',
    'src/components/Kpi/BaseSetModalDetail/ZhongHouTaiDept/KpiBaseSetZhongHouTaiDept.js',
  ],
  [
    'src/components/Kpi/BeautyTable/index.js',
    'src/components/Kpi/BeautyTable/KpiBeautyTable.js',
  ],
  [
    'src/components/Kpi/BusinessGoal/List/index.js',
    'src/components/Kpi/BusinessGoal/List/KpiBusinessGoalList.js',
  ],
  [
    'src/components/Kpi/ModalEditTable/index.js',
    'src/components/Kpi/ModalEditTable/KpiModalEditTable.js',
  ],
  [
    'src/components/Kpi/PmAssess/DetailContent/index.js',
    'src/components/Kpi/PmAssess/DetailContent/KpiPmAssessDetailContent.js',
  ],
  [
    'src/components/Kpi/PmAssess/EditModal/index.js',
    'src/components/Kpi/PmAssess/EditModal/KpiPmAssessEditModal.js',
  ],
  [
    'src/components/Kpi/PmAssess/List/index.js',
    'src/components/Kpi/PmAssess/List/KpiPmAssessList.js',
  ],
  [
    'src/components/Kpi/Estimation/departmentalPool/index.js',
    'src/components/Kpi/Estimation/departmentalPool/KpiEstimationDepartmentalPool.js',
  ],
  [
    'src/components/Kpi/Estimation/projectManagerPrize/index.js',
    'src/components/Kpi/Estimation/projectManagerPrize/KpiEstimationProjectManagerPrize.js',
  ],
  [
    'src/components/Kpi/Estimation/projectManagerProfit/index.js',
    'src/components/Kpi/Estimation/projectManagerProfit/KpiEstimationProjectManagerProfit.js',
  ],
  [
    'src/components/Kpi/ProjectAllot/AllocateInfo/index.js',
    'src/components/Kpi/ProjectAllot/AllocateInfo/KpiProjectAllotAllocateInfo.js',
  ],
  [
    'src/components/Kpi/ProjectAllot/BaseInfo/index.js',
    'src/components/Kpi/ProjectAllot/BaseInfo/KpiProjectAllotBaseInfo.js',
  ],
  [
    'src/components/Kpi/ProjectAllot/Detail/index.js',
    'src/components/Kpi/ProjectAllot/Detail/KpiProjectAllotDetail.js',
  ],
  [
    'src/components/Kpi/ProjectAllot/ProjectAllocateList/index.js',
    'src/components/Kpi/ProjectAllot/ProjectAllocateList/KpiProjectAllotProjectAllocateList.js',
  ],
  [
    'src/components/Kpi/ProjectAllot/BeforeAllocateInfoModal/index.js',
    'src/components/Kpi/ProjectAllot/BeforeAllocateInfoModal/KpiProjectAllotBeforeAllocateInfoModal.js',
  ],
  [
    'src/components/Kpi/ProjectAllot/ExtarInfo/index.js',
    'src/components/Kpi/ProjectAllot/ExtarInfo/KpiProjectAllotExtarInfo.js',
  ],
  [
    'src/components/Kpi/ProjectAllot/List/index.js',
    'src/components/Kpi/ProjectAllot/List/KpiProjectAllotListPage.js',
  ],
  [
    'src/components/Lease/ApprovalConfirm/index.js',
    'src/components/Lease/ApprovalConfirm/LeaseApprovalConfirmAction.js',
  ],
  [
    'src/components/Lease/Maintain/index.js',
    'src/components/Lease/Maintain/LeaseMaintainList.js',
  ],
  [
    'src/components/LifeCycle/CustomerList/index.js',
    'src/components/LifeCycle/CustomerList/LifeCycleCustomerList.js',
  ],
  [
    'src/components/Message/Notification/index.js',
    'src/components/Message/Notification/MessageNotification.js',
  ],
  [
    'src/components/Ocr/Recognition/index.js',
    'src/components/Ocr/Recognition/OcrRecognition.js',
  ],
  [
    'src/components/Overdue/Collection/List/index.js',
    'src/components/Overdue/Collection/List/OverdueCollectionList.js',
  ],
  [
    'src/components/Overdue/CollectionModal/index.js',
    'src/components/Overdue/CollectionModal/OverdueCollectionModal.js',
  ],
  [
    'src/components/Overdue/LitigationDoc/List/index.js',
    'src/components/Overdue/LitigationDoc/List/OverdueLitigationDocList.js',
  ],
  [
    'src/components/Overdue/LitigationDocAddModal/index.js',
    'src/components/Overdue/LitigationDocAddModal/OverdueLitigationDocAddModal.js',
  ],
  [
    'src/components/Overdue/LitigationRegistration/List/index.js',
    'src/components/Overdue/LitigationRegistration/List/OverdueLitigationRegistrationList.js',
  ],
  [
    'src/components/PaymentFtpColumns/index.js',
    'src/components/PaymentFtpColumns/FtpAssessmentColumns.js',
  ],
  [
    'src/components/Preview/PdfPreview/index.js',
    'src/components/Preview/PdfPreview/PdfPreview.js',
  ],
  [
    'src/components/Permission/Log/index.js',
    'src/components/Permission/Log/PermissionLog.js',
  ],
  [
    'src/components/Permission/Group/index.js',
    'src/components/Permission/Group/PermissionGroup.js',
  ],
  [
    'src/components/Report/FinancialReportApproval/index.js',
    'src/components/Report/FinancialReportApproval/ReportFinancialReportApproval.js',
  ],
  [
    'src/components/Report/FinancialReportList/index.js',
    'src/components/Report/FinancialReportList/ReportFinancialReportList.js',
  ],
  [
    'src/components/Report/Management/InternalHistory/index.js',
    'src/components/Report/Management/InternalHistory/ReportInternalHistory.js',
  ],
  [
    'src/components/Report/Management/Management/index.js',
    'src/components/Report/Management/Management/ReportManagement.js',
  ],
  [
    'src/components/Risk/PublicMonitorColumns/index.js',
    'src/components/Risk/PublicMonitorColumns/RiskPublicMonitorColumns.js',
  ],
  [
    'src/components/Risk/FinanceSheet/index.js',
    'src/components/Risk/FinanceSheet/RiskFinanceSheet.js',
  ],
  [
    'src/components/Risk/FinanceSheetFile/index.js',
    'src/components/Risk/FinanceSheetFile/RiskFinanceSheetFileList.js',
  ],
  [
    'src/components/Risk/MetricTimed/index.js',
    'src/components/Risk/MetricTimed/RiskMetricTimed.js',
  ],
  [
    'src/components/Risk/MetricValue/Control/index.js',
    'src/components/Risk/MetricValue/Control/RiskMetricControl.js',
  ],
  [
    'src/components/Risk/MetricValue/JinKon/index.js',
    'src/components/Risk/MetricValue/JinKon/RiskMetricJinKon.js',
  ],
  [
    'src/components/Risk/MetricValue/Target/index.js',
    'src/components/Risk/MetricValue/Target/RiskMetricValueTarget.js',
  ],
  [
    'src/components/Risk/OverdueListSearch/index.js',
    'src/components/Risk/OverdueListSearch/RiskOverdueListSearch.js',
  ],
  [
    'src/components/Risk/CloudMetricValue/Detail/index.js',
    'src/components/Risk/CloudMetricValue/Detail/RiskCloudMetricValueDetail.js',
  ],
  [
    'src/components/Risk/PublicMonitorDetail/index.js',
    'src/components/Risk/PublicMonitorDetail/RiskPublicMonitorDetail.js',
  ],
  [
    'src/components/Process/BlankBlock/index.js',
    'src/components/Process/BlankBlock/ProcessBlankBlock.js',
  ],
  [
    'src/components/Process/InfoModal/index.js',
    'src/components/Process/InfoModal/ProcessInfoModal.js',
  ],
  [
    'src/components/Process/ApprovalHistory/index.js',
    'src/components/Process/ApprovalHistory/ProcessApprovalHistory.js',
  ],
  [
    'src/components/Process/ApprovalHistoryModal/index.js',
    'src/components/Process/ApprovalHistoryModal/ProcessApprovalHistoryModal.js',
  ],
  [
    'src/components/Process/ProcessTypeTree/index.js',
    'src/components/Process/ProcessTypeTree/ProcessTypeTree.js',
  ],
  [
    'src/components/Process/TaskFlowChart/index.js',
    'src/components/Process/TaskFlowChart/ProcessTaskFlowChart.js',
  ],
  [
    'src/components/Process/RouteDetail/index.js',
    'src/components/Process/RouteDetail/ProcessRouteDetail.js',
  ],
  [
    'src/components/Process/RouteSnapshoot/index.js',
    'src/components/Process/RouteSnapshoot/ProcessRouteSnapshoot.js',
  ],
  [
    'src/components/Process/Snapshoot/index.js',
    'src/components/Process/Snapshoot/ProcessSnapshot.js',
  ],
  [
    'src/components/Process/ToSnapShoot/index.js',
    'src/components/Process/ToSnapShoot/ProcessToSnapShoot.js',
  ],
  [
    'src/components/Process/Query/index.js',
    'src/components/Process/Query/ProcessQuery.js',
  ],
  [
    'src/components/Project/FinancialReportStatistics/index.js',
    'src/components/Project/FinancialReportStatistics/ProjectFinancialReportStatistics.js',
  ],
  [
    'src/components/Project/Establishment/index.js',
    'src/components/Project/Establishment/ProjectEstablishment.js',
  ],
  [
    'src/components/Project/Price/index.js',
    'src/components/Project/Price/ProjectPrice.js',
  ],
  [
    'src/components/Project/Review/index.js',
    'src/components/Project/Review/ProjectReview.js',
  ],
  [
    'src/components/Project/DebtEvaluation/index.js',
    'src/components/Project/DebtEvaluation/ProjectDebtEvaluation.js',
  ],
  [
    'src/components/Project/FormListItem/index.js',
    'src/components/Project/FormListItem/ProjectFormListItem.js',
  ],
  [
    'src/components/Project/ReviewDetail/Data/index.js',
    'src/components/Project/ReviewDetail/Data/ProjectReviewDetailDataList.js',
  ],
  [
    'src/components/Project/ReviewDetail/CashFlowStatement/index.js',
    'src/components/Project/ReviewDetail/CashFlowStatement/ProjectReviewDetailCashFlowStatement.js',
  ],
  [
    'src/components/Project/ReviewDetail/Report/index.js',
    'src/components/Project/ReviewDetail/Report/ProjectReviewDetailReport.js',
  ],
  [
    'src/components/Project/ReviewMaterialTable/index.js',
    'src/components/Project/ReviewMaterialTable/ProjectReviewMaterialTable.js',
  ],
  [
    'src/components/Project/ReviewMeetingModal/index.js',
    'src/components/Project/ReviewMeetingModal/ProjectReviewMeetingModal.js',
  ],
  [
    'src/components/Project/ReviewSnapshot/index.js',
    'src/components/Project/ReviewSnapshot/ProjectReviewSnapshot.js',
  ],
  [
    'src/components/Risk/SourceCardCalcModal/index.js',
    'src/components/Risk/SourceCardCalcModal/RiskSourceCardCalcModal.js',
  ],
  [
    'src/components/Risk/SourceCard/List/index.js',
    'src/components/Risk/SourceCard/List/RiskSourceCardList.js',
  ],
  [
    'src/components/Risk/SourceCard/Detail/index.js',
    'src/components/Risk/SourceCard/Detail/RiskSourceCardDetail.js',
  ],
  [
    'src/components/Risk/PublicMonitorOpinionDetail/index.js',
    'src/components/Risk/PublicMonitorOpinionDetail/RiskPublicMonitorOpinionDetail.js',
  ],
  [
    'src/components/Risk/RiskStrategy/IndicatorManage/Detail/index.js',
    'src/components/Risk/RiskStrategy/IndicatorManage/Detail/RiskStrategyIndicatorDetail.js',
  ],
  [
    'src/components/Risk/RiskStrategy/ConcentrationControl/index.js',
    'src/components/Risk/RiskStrategy/ConcentrationControl/RiskStrategyConcentrationControl.js',
  ],
  [
    'src/components/Risk/RiskStrategy/RelateMonitor/index.js',
    'src/components/Risk/RiskStrategy/RelateMonitor/RiskStrategyRelateMonitor.js',
  ],
  [
    'src/components/TrackEvent/Tracking/detail/index.js',
    'src/components/TrackEvent/Tracking/detail/TrackEventDetail.js',
  ],
  [
    'src/components/TrackEvent/TrackModal/index.js',
    'src/components/TrackEvent/TrackModal/TrackEventModal.js',
  ],
  [
    'src/components/TrackEvent/Tracking/List/index.js',
    'src/components/TrackEvent/Tracking/List/TrackEventList.js',
  ],
  [
    'src/components/ChangeLogDiff/index.js',
    'src/components/ChangeLogDiff/ChangeLogDiff.js',
  ],
  [
    'src/components/VisitorManage/index.js',
    'src/components/VisitorManage/VisitorManagePage.js',
  ],
  [
    'src/components/WhiteList/Detail/index.js',
    'src/components/WhiteList/Detail/WhiteListDetail.js',
  ],
  [
    'src/components/WhiteList/List/index.js',
    'src/components/WhiteList/List/WhiteListList.js',
  ],
])
const removedLegacyStyleFiles = new Map([
  ['src/components/commonLess/animation.less', 'src/app.less'],
])
const legacyRouteStringRules = [
  {
    pattern: /customerView\/singeView|customer\/singeView/,
    replacement: 'customerView/singleView',
    allowedSourcePathPrefixes: ['src/pages/customerView/singeView/'],
    allowedSourcePaths: ['src/layout/SpecialPath.js'],
  },
]
const removedLegacyApiPathPrefixes = [
  {
    pathPrefix: 'src/api/afterLease/assessmentWhitelistApi',
    replacement: 'src/api/whiteList/assessmentWhitelistApi',
  },
  {
    pathPrefix: 'src/api/approval/processModifyRemarkApi',
    replacement: 'src/api/<domain>/approvalRemarkApi or src/api/common/approvalRemarkApi',
  },
  {
    pathPrefix: 'src/api/baseData/bankAccountApi',
    replacement: 'src/api/budget/bankAccountApi',
  },
  {
    pathPrefix: 'src/api/baseData/ftpMaterialsFile',
    replacement: 'src/api/budget/pricing/ftpMaterialsFile',
  },
  {
    pathPrefix: 'src/api/baseData/ftpQuarterlyGuidance',
    replacement: 'src/api/budget/pricing/ftpQuarterlyGuidance',
  },
  {
    pathPrefix: 'src/api/baseData/pricing/baseSet/ftpBaseSet',
    replacement: 'src/api/budget/pricing/baseSet/ftpBaseSet',
  },
  {
    pathPrefix: 'src/api/blackList',
    replacement: 'src/api/blackGray',
  },
  {
    pathPrefix: 'src/api/common/customerOverview',
    replacement: 'src/api/dashboard/customerOverview or src/api/customerView/customerOverviewApi',
  },
  {
    pathPrefix: 'src/api/common/flowList',
    replacement: 'src/api/process/flowTaskApi',
  },
  {
    pathPrefix: 'src/api/common/editableCompare',
    replacement: 'src/api/common/fileCompareApi',
  },
  {
    pathPrefix: 'src/api/common/dataList',
    replacement: 'src/api/common/materialsApi',
  },
  {
    pathPrefix: 'src/api/common/irrGenerationApi',
    replacement: 'src/api/layout/irrGenerationApi',
  },
  {
    pathPrefix: 'src/api/common/interface/irrGenerationApi',
    replacement: 'src/api/layout/irrGenerationApi',
  },
  {
    pathPrefix: 'src/api/common/workbenchApi',
    replacement: 'src/api/dashboard/userCustomConfigApi or src/api/dashboard/feikongSsoApi',
  },
  {
    pathPrefix: 'src/api/dashboard/workbench',
    replacement: 'src/api/dashboard/userCustomConfigApi, src/api/dashboard/workbenchMessageApi, or dashboard semantic APIs',
  },
  {
    pathPrefix: 'src/api/cpm/payment/contractPaymentFtp',
    replacement: 'src/api/contract/payment/contractPaymentFtp',
  },
  {
    pathPrefix: 'src/api/fillingMaterials',
    replacement: 'src/api/filingMaterials',
  },
  {
    pathPrefix: 'src/api/financial/accountsReceivable',
    replacement: 'src/api/budget/accountsReceivable',
  },
  {
    pathPrefix: 'src/api/financialReport',
    replacement: 'src/api/report',
  },
  {
    pathPrefix: 'src/api/groupCredit/common',
    replacement: 'src/api/common/selectApi',
  },
  {
    pathPrefix: 'src/api/groupCredit/projectApprovalBaseinfo',
    replacement: 'src/api/credit/groupCreditEstablishApi',
  },
  {
    pathPrefix: 'src/api/groupCredit/projectApprovalReport',
    replacement: 'src/api/credit/groupCreditEstablishReportApi',
  },
  {
    pathPrefix: 'src/api/groupCredit/projectApprovalVersion',
    replacement: 'src/api/credit/groupCreditEstablishVersionApi',
  },
  {
    pathPrefix: 'src/api/header/projProfitTool',
    replacement: 'src/api/layout/projProfitToolApi',
  },
  {
    pathPrefix: 'src/api/kpi/projProfit',
    replacement: 'src/api/budget/projectProfit* or src/api/layout/projProfitToolApi',
  },
  {
    pathPrefix: 'src/api/kpi/baseSet/parameterConfig',
    replacement: 'src/api/budget/projectProfitBaseSetApi',
  },
  {
    pathPrefix: 'src/api/lease/evaluationAgencyApi',
    replacement: 'src/api/evaluationAgency/evaluationAgencyApi',
  },
  {
    pathPrefix: 'src/api/lease/trackingApi',
    replacement: 'src/api/trackEvent/trackingApi',
  },
  {
    pathPrefix: 'src/api/liquidity',
    replacement: 'src/api/financial/liquidity',
  },
  {
    pathPrefix: 'src/api/manageReport',
    replacement: 'src/api/report',
  },
  {
    pathPrefix: 'src/api/newFtp',
    replacement: 'src/api/budget/pricing/ftp',
  },
  {
    pathPrefix: 'src/api/postRentalInspection',
    replacement: 'src/api/afterLease',
  },
  {
    pathPrefix: 'src/api/pricing',
    replacement: 'src/api/budget/pricing',
  },
  {
    pathPrefix: 'src/api/riskControl',
    replacement: 'src/api/risk',
  },
  {
    pathPrefix: 'src/api/risk/customerUnifiedViewController',
    replacement: 'src/api/customerView/riskAreaApi or src/api/customerView/customerDetailApi',
  },
  {
    pathPrefix: 'src/api/risk/interface/customerUnifiedViewController',
    replacement: 'src/api/customerView/riskAreaApi or src/api/customerView/customerDetailApi',
  },
  {
    pathPrefix: 'src/api/workbench',
    replacement: 'src/api/dashboard/workbench',
  },
]

const privateComponentPathPattern = /^@\/components\/[^'"]+\/(?:api|store|context|config|Config|Column|columns)(?:\.js)?$/
const deepComponentPathPattern = /^@\/components\/[^'"]+\/[^'"]+\/[^'"]+\/[^'"]+/
const sharedComponentSubpathPattern =
  /^@\/components\/(?:Actions|Form|Format|Table)\/[^'"]+|^@\/components\/Chart\/tooltip$/
const nonEntryComponentSubpathPattern =
  /^@\/components\/[^/'"]+\/(?![^/'"]*(?:Entries|entries)(?:\.js)?$)[^/'"]+(?:\.js)?$/
const componentEntryPathPattern =
  /^@\/components\/([^/'"]+)\/[^/'"]*(?:Entries|entries)(?:\.js)?$/
const componentEntryReExportOnlyPattern =
  /^\s*(?:export\s+\{[^}]+\}\s+from\s+['"][^'"]+['"]\s*;?\s*)+$/
const componentEntryAbsoluteComponentImportPattern = /from\s+['"]@\/components\//
const stabilizedComponentRootImports = new Map([
  ['BlackGrayHit', 'BlackGray/BlackGrayHitEntries'],
  ['BusinessInfoCheck', 'BusinessInfoCheck/BusinessInfoCheckEntries'],
  ['ClientFileTable', 'ClientMaterialTable/BusinessMaterialTableEntries'],
  ['ClientMaterialTable', 'ClientMaterialTable/BusinessMaterialTableEntries'],
  ['ChangeLogDiff', 'ChangeLogDiff/ChangeLogDiffEntries'],
  ['Dashboard', 'Dashboard/*Entries.js'],
  ['EvaluationAgency', 'EvaluationAgency/AppraisalAgencyEntries'],
  ['FileDiff', 'ChangeLogDiff/ChangeLogDiffEntries'],
  ['InsurancePolicy', 'InsurancePolicy/InsurancePolicyEntries'],
  ['PaymentApplyColumns', 'PaymentFtpColumns/FtpAssessmentColumnsEntries'],
  ['PaymentFtpColumns', 'PaymentFtpColumns/FtpAssessmentColumnsEntries'],
  ['Policy', 'InsurancePolicy/InsurancePolicyEntries'],
  ['TrackEvent', 'TrackEvent/TrackEventListEntries'],
])
const stableTableRootImports = new Set([
  'ApprovalDetail',
  'CRUDTable',
  'DetailTable',
  'DynamicDesc',
  'EditDescription',
  'EditTable',
  'FileTable',
  'FileTableMe',
  'NoEnumFileTable',
  'Summary',
  'VersionTable',
])
const stableSelectRootImports = new Set([
  'ApiSelect',
  'ClientSelect',
  'ContractSelect',
  'FounderSelect',
  'OrgSelect',
  'OrgSelectZh',
  'ProjectReviewSelect',
  'ProvinceSelect',
  'RoleSelect',
  'getOrgList',
  'getOrgList2',
])
const stableDefaultComponentRootImports = new Map([
  ['Amount', 'Amount'],
  ['Collapse', 'Collapse'],
  ['CommonNoData', 'CommonNoData'],
  ['CommonTips', 'CommonTips'],
  ['CurrentSteps', 'CurrentSteps'],
  ['DetailLayout', 'DetailLayout'],
  ['FormulaValueTip', 'FormulaValueTip'],
  ['PageListDown', 'PageListDown'],
  ['ReadOnly', 'ReadOnly'],
  ['RegionCascader', 'RegionCascader'],
  ['ZInput', 'ZInput'],
])
const publicComponentRootImports = new Set([
  'Actions',
  'Amount',
  'AmountRange',
  'Collapse',
  'CommonNoData',
  'CommonTips',
  'CurrentSteps',
  'DataUpload',
  'DetailLayout',
  'Excel',
  'FileList',
  'Form',
  'FormIrr',
  'FormItemContent',
  'FormUpload',
  'Format',
  'FormulaValueTip',
  'Icon',
  'PageListDown',
  'ReadOnly',
  'RegionCascader',
  'RenderColumn',
  'RepayCalcType',
  'Select',
  'StarDom',
  'Table',
  'ZInput',
])
const componentRootImportPattern = /^@\/components\/([^/'"]+)$/
const pageImportPattern = /^@\/pages\//
const publicStyleImports = new Set()
const legacyUtilityPrefixRules = [
  {
    legacyPrefix: '@/utils/afterLease',
    replacementPrefix: '@/utils/domains/afterLease/AfterLeaseUtils',
  },
  {
    legacyPrefix: '@/utils/budgetManagement',
    replacementPrefix: '@/utils/domains/budgetManagement/BudgetManagementUtils',
  },
  {
    legacyPrefix: '@/utils/domains/budgetManagement/BudgetManagementUtils',
    replacementPrefix: '@/utils/domains/budget/ProvisionForecastUtils',
  },
  {
    legacyPrefix: '@/utils/customer',
    replacementPrefix: '@/utils/domains/customer/CustomerUtils',
  },
  {
    legacyPrefix: '@/utils/customerRat',
    replacementPrefix: '@/utils/domains/customer/CustomerRatUtils',
  },
  {
    legacyPrefix: '@/utils/dashboard',
    replacementPrefix: '@/utils/domains/dashboard/DashboardUtils*',
  },
  {
    legacyPrefix: '@/utils/dashboardColumns',
    replacementPrefix: '@/utils/domains/dashboard/DashboardUtilsColumns',
  },
  {
    legacyPrefix: '@/utils/dashboardFilterKeys',
    replacementPrefix: '@/utils/domains/dashboard/DashboardUtilsFilterKeys',
  },
  {
    legacyPrefix: '@/utils/dashboardOperation',
    replacementPrefix: '@/utils/domains/dashboard/DashboardUtilsOperation',
  },
  {
    legacyPrefix: '@/utils/kpi',
    replacementPrefix: '@/utils/domains/kpi/KpiUtils',
  },
  {
    legacyPrefix: '@/utils/paymentApplication',
    replacementPrefix: '@/utils/domains/cpm/PaymentApplicationUtils',
  },
  {
    legacyPrefix: '@/utils/processFlow',
    replacementPrefix: '@/utils/domains/process/ProcessFlowContext',
  },
  {
    legacyPrefix: '@/utils/report',
    replacementPrefix: '@/utils/domains/report/ReportUtils',
  },
  {
    legacyPrefix: '@/utils/risk',
    replacementPrefix: '@/utils/domains/risk/RiskUtils',
  },
  {
    legacyPrefix: '@/utils/rzyConfig',
    replacementPrefix: '@/utils/domains/rzy/RzyConfig',
  },
  {
    legacyPrefix: '@/utils/options/financialReport',
    replacementPrefix: '@/utils/domains/report/* when report options are needed',
  },
  {
    legacyPrefix: '@/utils/options/ftp',
    replacementPrefix: '@/utils/domains/budget/* when FTP pricing options are needed',
  },
  {
    legacyPrefix: '@/utils/hooks/useGetColumns',
    replacementPrefix: 'domain component-local options helpers when needed',
  },
  {
    legacyPrefix: '@/utils/hooks/useGetStatus',
    replacementPrefix: '@/utils/domains/blackGray/BlackGrayStatusUtils',
  },
  {
    legacyPrefix: '@/utils/hooks/useLayoutEffect',
    replacementPrefix: 'React.useLayoutEffect or domain-local hooks when needed',
  },
]
const legacyApiDomains = new Map([
  ['blackList', 'blackGray'],
  ['financialReport', 'report'],
  ['fillingMaterials', 'filingMaterials'],
  ['header', 'kpi/projProfit'],
  ['liquidity', 'financial/liquidity'],
  ['manageReport', 'report'],
  ['newFtp', 'budget/pricing/ftp'],
  ['postRentalInspection', 'afterLease'],
  ['pricing', 'budget/pricing'],
  ['riskControl', 'risk'],
  ['workbench', 'dashboard'],
])
const legacyApiPrefixRules = [
  {
    legacyPrefix: '@/api/cpm/payment/contractPaymentFtp',
    replacementPrefix: '@/api/contract/payment/contractPaymentFtp',
    allowedSourceDomains: ['Cpm', 'cpm'],
  },
  {
    legacyPrefix: '@/api/financial/accountsReceivable',
    replacementPrefix: '@/api/budget/accountsReceivable',
  },
  {
    legacyPrefix: '@/api/lease/trackingApi',
    replacementPrefix: '@/api/trackEvent/trackingApi',
  },
  {
    legacyPrefix: '@/api/lease/evaluationAgencyApi',
    replacementPrefix: '@/api/evaluationAgency/evaluationAgencyApi',
  },
  {
    legacyPrefix: '@/api/evaluationAgency/evaluationAgencyApi',
    replacementPrefix: '@/api/whiteList/appraisalCompanyApi, @/api/lease/evaluationAgencyMaintainApi, or @/api/evaluationAgency/evaluationAgencyApi in EvaluationAgency domain',
    allowedSourceDomains: ['EvaluationAgency'],
    allowedSourcePathPrefixes: [
    ],
  },
  {
    legacyPrefix: '@/api/afterLease/assessmentWhitelistApi',
    replacementPrefix: '@/api/whiteList/assessmentWhitelistApi',
  },
  {
    legacyPrefix: '@/api/whiteList/assessmentWhitelistApi',
    replacementPrefix:
      '@/api/evaluationAgency/assessmentWhitelistApi or @/api/whiteList/assessmentWhitelistApi in WhiteList domain',
    allowedSourceDomains: ['WhiteList'],
    allowedSourcePathPrefixes: [
      'src/pages/whiteList/',
    ],
  },
  {
    legacyPrefix: '@/api/ocr/ocrInvoiceApi',
    replacementPrefix: '@/api/lease/vatInvoiceApi',
    allowedSourceDomains: ['Ocr'],
    allowedSourcePathPrefixes: [
      'src/pages/ocr/',
    ],
  },
  {
    legacyPrefix: '@/api/process/flowExecution',
    replacementPrefix: '@/api/customer/customerRat/customerRatApprovalApi',
    allowedSourcePathPrefixes: [
      'src/pages/process/',
      'src/components/Process/',
    ],
  },
  {
    legacyPrefix: '@/api/contract/baseInfo',
    replacementPrefix:
      '@/api/budget/contractInfoApi, @/api/trackEvent/contractInfoApi, or @/api/contract/baseInfo in Contract domain',
    allowedSourceDomains: ['Contract'],
    allowedSourcePathPrefixes: [
      'src/pages/contract/',
    ],
  },
  {
    legacyPrefix: '@/api/contract/contractDetail',
    replacementPrefix:
      '@/api/process/detail/contractDetailApi or @/api/contract/contractDetail in Contract domain',
    allowedSourceDomains: ['Contract'],
    allowedSourcePathPrefixes: [
      'src/pages/contract/',
    ],
  },
  {
    legacyPrefix: '@/api/financial/fundApi',
    replacementPrefix: '@/api/contract/priceApi or @/api/financial/fundApi in Financial domain',
    allowedSourceDomains: ['Financial'],
    allowedSourcePathPrefixes: [
      'src/api/contract/priceApi.js',
      'src/pages/financial/',
    ],
  },
  {
    legacyPrefix: '@/api/filingMaterials/otherFilingMaterialsDetail',
    replacementPrefix:
      '@/api/process/detail/filingMaterialsApi or @/api/filingMaterials/otherFilingMaterialsDetail in FilingMaterials domain',
    allowedSourceDomains: ['FilingMaterials'],
    allowedSourcePathPrefixes: [
      'src/pages/fillingMaterialsDetail/',
    ],
  },
  {
    legacyPrefix: '@/api/approval/processModifyRemarkApi',
    replacementPrefix:
      '@/api/<domain>/approvalRemarkApi or @/api/common/approvalRemarkApi for shared approval components',
    allowedSourcePathPrefixes: [
      'src/api/common/approvalRemarkApi.ts',
    ],
  },
  {
    legacyPrefix: '@/api/baseData/pricing/baseSet/ftpBaseSet',
    replacementPrefix: '@/api/budget/pricing/baseSet/ftpBaseSet',
  },
  {
    legacyPrefix: '@/api/baseData/ftpMaterialsFile',
    replacementPrefix: '@/api/budget/pricing/ftpMaterialsFile',
  },
  {
    legacyPrefix: '@/api/baseData/ftpQuarterlyGuidance',
    replacementPrefix: '@/api/budget/pricing/ftpQuarterlyGuidance',
  },
  {
    legacyPrefix: '@/api/baseData/bankAccountApi',
    replacementPrefix: '@/api/budget/bankAccountApi',
    allowedSourcePathPrefixes: ['src/api/budget/bankAccountApi.ts'],
  },
  {
    legacyPrefix: '@/api/baseData/fileTemplateApi',
    replacementPrefix: '@/api/baseData/fileTemplateApi in BaseData domain',
    allowedSourcePathPrefixes: [
      'src/components/BaseData/',
      'src/pages/baseData/',
    ],
  },
  {
    legacyPrefix: '@/api/baseData/leaseholdProperty',
    replacementPrefix: '@/api/baseData/leaseholdProperty in BaseData domain',
    allowedSourcePathPrefixes: [
      'src/components/BaseData/',
      'src/pages/baseData/',
    ],
  },
  {
    legacyPrefix: '@/api/budget/pricing/ftpInterestChangeApi',
    replacementPrefix:
      '@/api/process/detail/ftpInterestChangeApi or @/api/budget/pricing/ftpInterestChangeApi in Budget domain',
    allowedSourceDomains: ['Budget', 'budget'],
    allowedSourcePathPrefixes: [
      'src/pages/budget/',
    ],
  },
  {
    legacyPrefix: '@/api/groupCredit/common',
    replacementPrefix: '@/api/common/selectApi',
  },
  {
    legacyPrefix: '@/api/groupCredit/projectApprovalBaseinfo',
    replacementPrefix: '@/api/credit/groupCreditEstablishApi',
    allowedSourcePathPrefixes: ['src/api/credit/groupCreditEstablishApi.ts'],
  },
  {
    legacyPrefix: '@/api/groupCredit/projectApprovalReport',
    replacementPrefix: '@/api/credit/groupCreditEstablishReportApi',
    allowedSourcePathPrefixes: ['src/api/credit/groupCreditEstablishReportApi.ts'],
  },
  {
    legacyPrefix: '@/api/groupCredit/projectApprovalVersion',
    replacementPrefix: '@/api/credit/groupCreditEstablishVersionApi',
    allowedSourcePathPrefixes: ['src/api/credit/groupCreditEstablishVersionApi.ts'],
  },
  {
    legacyPrefix: '@/api/kpi/projProfit/profitCalculateTool',
    replacementPrefix: '@/api/layout/projProfitToolApi',
    allowedSourcePathPrefixes: ['src/api/layout/projProfitToolApi.ts'],
  },
  {
    legacyPrefix: '@/api/kpi/projProfit/projProfit',
    replacementPrefix: '@/api/budget/projectProfitApi',
    allowedSourcePathPrefixes: ['src/api/budget/projectProfitApi.ts'],
  },
  {
    legacyPrefix: '@/api/kpi/projProfit/kpiParameterConfigApi',
    replacementPrefix: '@/api/budget/projectProfitParameterApi',
    allowedSourcePathPrefixes: ['src/api/budget/projectProfitParameterApi.ts'],
  },
  {
    legacyPrefix: '@/api/kpi/baseSet/parameterConfig',
    replacementPrefix: '@/api/budget/projectProfitBaseSetApi',
    allowedSourcePathPrefixes: ['src/api/budget/projectProfitBaseSetApi.js'],
  },
  {
    legacyPrefix: '@/api/message/messageNotification',
    replacementPrefix: '@/api/layout/messageApi, @/api/dashboard/workbenchMessageApi, or @/api/message/messageNotification in Message domain',
    allowedSourcePathPrefixes: [
      'src/components/Message/',
      'src/pages/msgNotification/',
    ],
  },
  {
    legacyPrefix: '@/api/visitorManage/visitorManageApi',
    replacementPrefix: '@/api/visitorManage/visitorManageApi in VisitorManage domain',
    allowedSourcePathPrefixes: [
      'src/components/VisitorManage/',
      'src/pages/visitorManage/',
    ],
  },
  {
    legacyPrefix: '@/api/permission/login',
    replacementPrefix: '@/api/layout/fastLoginApi or @/api/permission/login in Login page',
    allowedSourcePathPrefixes: [
      'src/components/Permission/Auth/',
      'src/pages/login/',
    ],
  },
  {
    legacyPrefix: '@/api/blackGray/queryExternalDataApi',
    replacementPrefix: '@/api/customerView/blackGrayApi or @/api/blackGray/queryExternalDataApi in BlackGray domain',
    allowedSourceDomains: ['BlackGray'],
    allowedSourcePathPrefixes: [
      'src/pages/blackListManage/',
    ],
  },
  {
    legacyPrefix: '@/api/risk/customerUnifiedViewController',
    replacementPrefix: '@/api/customerView/riskAreaApi or @/api/customerView/customerDetailApi',
  },
  {
    legacyPrefix: '@/api/risk/monitorEarly',
    replacementPrefix: '@/api/customerView/riskWarningApi or @/api/risk/monitorEarly in Risk monitor pages',
    allowedSourcePathPrefixes: [
      'src/components/Risk/MonitorEarly/',
      'src/pages/monitorEarly/',
    ],
  },
  {
    legacyPrefix: '@/api/risk/publicMonitor',
    replacementPrefix:
      '@/api/lifeCycle/riskWarningApi, @/api/process/detail/publicMonitorApi, or @/api/risk/publicMonitor in Risk domain',
    allowedSourceDomains: ['Risk'],
    allowedSourcePathPrefixes: [
      'src/pages/risk/',
    ],
  },
  {
    legacyPrefix: '@/api/overdue/collectionManagementApi',
    replacementPrefix:
      '@/api/process/detail/overdueCollectionApi or @/api/overdue/collectionManagementApi in Overdue domain',
    allowedSourceDomains: ['Overdue'],
    allowedSourcePathPrefixes: [
      'src/pages/overdue/',
    ],
  },
  {
    legacyPrefix: '@/api/overdue/sealForDocumentsApi',
    replacementPrefix:
      '@/api/process/detail/overdueSealDocumentApi or @/api/overdue/sealForDocumentsApi in Overdue domain',
    allowedSourceDomains: ['Overdue'],
    allowedSourcePathPrefixes: [
      'src/pages/overdue/',
    ],
  },
  {
    legacyPrefix: '@/api/cpm/payment/paymentApplicationDetail',
    replacementPrefix:
      '@/api/process/detail/paymentApplicationDetailApi, @/api/process/operation/paymentOperationApi, or @/api/cpm/payment/paymentApplicationDetail in Cpm domain',
    allowedSourceDomains: ['Cpm', 'cpm'],
    allowedSourcePathPrefixes: [
      'src/utils/domains/cpm/PaymentApplicationUtils.js',
      'src/pages/cpm/',
      'src/pages/process/Detail/ZTabs/Operation/',
    ],
  },
  {
    legacyPrefix: '@/api/cpm/payment/publicInfoApi',
    replacementPrefix:
      '@/api/process/operation/paymentPublicInfoApi or @/api/cpm/payment/publicInfoApi in Cpm domain',
    allowedSourceDomains: ['Cpm', 'cpm'],
    allowedSourcePathPrefixes: [
      'src/components/Cpm/',
      'src/pages/cpm/',
    ],
  },
  {
    legacyPrefix: '@/api/project/projReviewDetail',
    replacementPrefix:
      '@/api/process/detail/projectReviewDetailApi or @/api/project/projReviewDetail in Project domain',
    allowedSourceDomains: ['Project'],
    allowedSourcePathPrefixes: [
      'src/pages/project/',
    ],
  },
  {
    legacyPrefix: '@/api/project/projReviewFinancialReport',
    replacementPrefix:
      '@/api/process/operation/projectReviewFinancialReportApi or @/api/project/projReviewFinancialReport in Project domain',
    allowedSourceDomains: ['Project'],
    allowedSourcePathPrefixes: [
      'src/components/Project/',
      'src/pages/project/',
    ],
  },
  {
    legacyPrefix: '@/api/project/projReviewMeetingMinute',
    replacementPrefix:
      '@/api/process/detail/projectReviewMeetingMinuteApi or @/api/project/projReviewMeetingMinute in Project domain',
    allowedSourceDomains: ['Project'],
    allowedSourcePathPrefixes: [
      'src/pages/project/',
    ],
  },
  {
    legacyPrefix: '@/api/customer/customerRat/debtRatApi',
    replacementPrefix:
      '@/api/process/detail/debtRatingApi or @/api/customer/customerRat/debtRatApi in Customer/process utilities',
    allowedSourceDomains: ['Customer'],
    allowedSourcePathPrefixes: [
      'src/pages/customer/',
      'src/pages/process/Detail/ZTabs/Operation/Components/Operator/',
    ],
  },
  {
    legacyPrefix: '@/api/customer/customerRat/customerRatApi',
    replacementPrefix:
      '@/api/project/ratingApi for project rating updates, @/api/process/detail/customerRatingApi, or @/api/customer/customerRat/customerRatApi in Customer/process utilities',
    allowedSourceDomains: ['Customer'],
    allowedSourcePathPrefixes: [
      'src/api/project/ratingApi.js',
      'src/utils/domains/customer/CustomerRatUtils.js',
      'src/pages/customer/',
      'src/pages/process/',
      'src/utils/customerRat.js',
    ],
  },
  {
    legacyPrefix: '@/api/customer/clientBasic',
    replacementPrefix: '@/api/common/selectApi for select dictionaries or @/api/customer/clientBasic in Customer domain',
    allowedSourceDomains: ['Customer'],
    allowedSourcePathPrefixes: [
      'src/pages/customer/',
    ],
  },
  {
    legacyPrefix: '@/api/customer/maintainApi',
    replacementPrefix:
      '@/api/process/application/customerMaintainApi or @/api/customer/maintainApi in Customer domain',
    allowedSourceDomains: ['Customer'],
    allowedSourcePathPrefixes: [
      'src/components/Customer/',
      'src/pages/customer/',
    ],
  },
  {
    legacyPrefix: '@/api/common/flowList',
    replacementPrefix: '@/api/process/flowTaskApi',
  },
  {
    legacyPrefix: '@/api/common/editableCompare',
    replacementPrefix: '@/api/common/fileCompareApi for file compare APIs',
  },
  {
    legacyPrefix: '@/api/common/dataList',
    replacementPrefix: '@/api/common/materialsApi',
  },
  {
    legacyPrefix: '@/api/budget/flowCenter/bankFlowProcessingCenterApi',
    replacementPrefix: '@/api/cpm/payment/writeOffFlowCenterApi',
    allowedSourceDomains: ['Budget'],
    allowedSourcePathPrefixes: [
      'src/pages/budget/flowCenter/',
    ],
  },
  {
    legacyPrefix: '@/api/budget/flowCenter/flowCenterApi',
    replacementPrefix: '@/api/cpm/payment/writeOffFlowCenterApi',
    allowedSourceDomains: ['Budget'],
    allowedSourcePathPrefixes: [
      'src/pages/budget/flowCenter/',
    ],
  },
]
const legacyApiImportPattern = /^@\/api\/([^/'"]+)(?:\/|$)/
const apiInterfaceImportPattern = /^@\/api\/[^'"]+\/interface\//
const allowedZeroIncomingApiFiles = new Set([
  'src/api/budget/pricing/ftpMaterialsFile.ts',
  'src/api/common/materialsApi.ts',
])
const routeDomainAliases = createDomainAliases()

function normalizeEntryPath(filePath) {
  return path.relative(path.join(srcDir, 'components'), filePath).split(path.sep).join('/')
}

function toDocumentedEntryPath(specifier) {
  const [, domain, entry] =
    specifier.match(/^@\/components\/([^/'"]+)\/([^/'"]*(?:Entries|entries)(?:\.js)?)$/) || []

  if (!domain || !entry) {
    return null
  }

  return `${domain}/${entry.endsWith('.js') ? entry : `${entry}.js`}`
}

function walk(dir, files = []) {
  if (!fs.existsSync(dir)) {
    return files
  }

  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const filePath = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      walk(filePath, files)
    } else if (scannableFilePattern.test(entry.name)) {
      files.push(filePath)
    }
  }

  return files
}

function walkMatchingFiles(dir, pattern, files = []) {
  if (!fs.existsSync(dir)) {
    return files
  }

  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const filePath = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      walkMatchingFiles(filePath, pattern, files)
    } else if (pattern.test(entry.name)) {
      files.push(filePath)
    }
  }

  return files
}

function findEmptyDirs(dir, emptyDirs = []) {
  if (!fs.existsSync(dir)) {
    return emptyDirs
  }

  const entries = fs.readdirSync(dir, { withFileTypes: true })
  for (const entry of entries) {
    if (entry.isDirectory()) {
      findEmptyDirs(path.join(dir, entry.name), emptyDirs)
    }
  }

  if (entries.length === 0) {
    emptyDirs.push(dir)
  }

  return emptyDirs
}

function extractSpecifiers(source, relativeFilePath) {
  const specifiers = []
  const pattern = /\.less$/.test(relativeFilePath) ? styleImportPattern : importPattern
  let match

  while ((match = pattern.exec(source))) {
    specifiers.push({
      importText: match[0],
      specifier: match[1],
    })
  }

  return specifiers
}

function resolveSourceImport(filePath, specifier) {
  let basePath

  if (specifier.startsWith('@/')) {
    basePath = path.join(srcDir, specifier.slice(2))
  } else if (specifier.startsWith('.')) {
    basePath = path.resolve(path.dirname(filePath), specifier)
  } else {
    return null
  }

  const candidates = []
  if (path.extname(basePath)) {
    candidates.push(basePath)
  } else {
    for (const extension of sourceExtensions) {
      candidates.push(`${basePath}${extension}`)
    }
    for (const extension of sourceExtensions) {
      candidates.push(path.join(basePath, `index${extension}`))
    }
  }

  return candidates.find((candidate) => fs.existsSync(candidate)) || null
}

function getLegacyApiPrefixRule(specifier) {
  for (const rule of legacyApiPrefixRules) {
    const { legacyPrefix } = rule
    if (specifier === legacyPrefix || specifier.startsWith(`${legacyPrefix}/`)) {
      return rule
    }
  }

  return null
}

function getLegacyUtilityPrefixRule(specifier) {
  for (const rule of legacyUtilityPrefixRules) {
    const { legacyPrefix } = rule
    if (specifier === legacyPrefix || specifier.startsWith(`${legacyPrefix}/`)) {
      return rule
    }
  }

  return null
}

function isAllowedLegacyApiPrefixSource(rule, relativeFilePath, sourceComponentDomain) {
  return (
    rule.allowedSourceDomains?.includes(sourceComponentDomain) ||
    rule.allowedSourcePathPrefixes?.some((prefix) => relativeFilePath.startsWith(prefix))
  )
}

function extractNamedImports(importText) {
  const [, rawNamedImports] = importText.match(/\{([^}]+)\}/) || []
  if (!rawNamedImports) {
    return []
  }

  return rawNamedImports
    .split(',')
    .map((name) => name.trim().split(/\s+as\s+/)[0])
    .filter(Boolean)
}

function hasConsoleLog(source) {
  return source.includes('console.log(')
}

function hasDebugger(source) {
  return /\bdebugger\b/.test(source)
}

function matchesSourcePathPrefix(relativeFilePath, pathPrefix) {
  return (
    relativeFilePath === pathPrefix ||
    relativeFilePath.startsWith(`${pathPrefix}/`) ||
    sourceExtensions.some((extension) => relativeFilePath === `${pathPrefix}${extension}`)
  )
}

function isPublicComponentSourceFile(relativeFilePath) {
  const [, componentRoot] =
    relativeFilePath.match(/^src[\\/]components[\\/]([^\\/]+)[\\/].*\.(?:js|jsx|ts|tsx)$/) ||
    relativeFilePath.match(/^src[\\/]components[\\/]([^\\/]+)\.(?:js|jsx|ts|tsx)$/) ||
    []

  return publicComponentRootImports.has(componentRoot)
}

function upperFirst(value) {
  return value ? `${value[0].toUpperCase()}${value.slice(1)}` : value
}

const violations = []
const sourceFiles = scanDirs.flatMap((dir) => walk(dir))
const styleFiles = scanDirs.flatMap((dir) => walkMatchingFiles(dir, styleFilePattern))
const pageFiles = walkMatchingFiles(path.join(srcDir, 'pages'), allFilePattern)
const componentDomains = new Set(
  fs
    .readdirSync(path.join(srcDir, 'components'), { withFileTypes: true })
    .filter((entry) => entry.isDirectory())
    .map((entry) => entry.name)
)
const componentDomainKeys = new Set(
  [...componentDomains].map((domain) => `${domain[0].toLowerCase()}${domain.slice(1)}`)
)
const pageRouteDomains = fs
  .readdirSync(path.join(srcDir, 'pages'), { withFileTypes: true })
  .filter((entry) => entry.isDirectory())
  .map((entry) => entry.name)

for (const routeDomain of pageRouteDomains) {
  const aliasedDomain = routeDomainAliases.get(routeDomain)
  const aliasedComponentDomain = upperFirst(aliasedDomain)
  if (!componentDomainKeys.has(routeDomain) && !componentDomains.has(aliasedComponentDomain)) {
    violations.push({
      file: `src/pages/${routeDomain}`,
      specifier:
        'page route domain must match src/components/<Domain> or be declared in scripts/domain-report-config.js',
    })
  }
}

for (const filePath of pageFiles) {
  const relativeFilePath = path.relative(root, filePath)
  if (!sourceFilePattern.test(filePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'non-route file in src/pages (move implementation, styles, and assets to src/components/<Domain>)',
    })
  }
}

for (const filePath of styleFiles) {
  if (fs.statSync(filePath).size === 0) {
    violations.push({
      file: path.relative(root, filePath),
      specifier: 'empty style file',
    })
  }
}

for (const dirPath of [
  ...findEmptyDirs(path.join(srcDir, 'components')),
  ...findEmptyDirs(path.join(srcDir, 'pages')),
]) {
  violations.push({
    file: path.relative(root, dirPath),
    specifier: 'empty source directory',
  })
}

for (const filePath of sourceFiles) {
  const relativeFilePath = path.relative(root, filePath)
  const source = fs.readFileSync(filePath, 'utf8')
  if (copiedSourceFilePattern.test(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'copied or backup source file',
    })
  }

  if (sampleSourceFilePattern.test(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'mock/demo/example source file',
    })
  }

  if (removedLegacyUtilityFiles.has(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: `removed legacy utility file (use ${removedLegacyUtilityFiles.get(relativeFilePath)})`,
    })
  }

  if (removedLegacyComponentFiles.has(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: `removed legacy component file (use ${removedLegacyComponentFiles.get(relativeFilePath)})`,
    })
  }

  const legacyRouteStringRule = legacyRouteStringRules.find(
    (rule) =>
      rule.pattern.test(source) &&
      !rule.allowedSourcePaths?.includes(relativeFilePath) &&
      !rule.allowedSourcePathPrefixes?.some((prefix) => relativeFilePath.startsWith(prefix))
  )
  if (legacyRouteStringRule) {
    violations.push({
      file: relativeFilePath,
      specifier: `legacy route string (use ${legacyRouteStringRule.replacement})`,
    })
  }

  const removedLegacyApiPath = removedLegacyApiPathPrefixes.find(
    ({ pathPrefix }) => matchesSourcePathPrefix(relativeFilePath, pathPrefix)
  )
  if (removedLegacyApiPath) {
    violations.push({
      file: relativeFilePath,
      specifier: `removed legacy api path (use ${removedLegacyApiPath.replacement})`,
    })
  }

  if (hasConsoleLog(source)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'console.log debug residue in frontend source',
    })
  }

  if (hasDebugger(source)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'debugger residue in frontend source',
    })
  }

  if (
    /^src[\\/]pages[\\/].*\.(?:js|jsx|ts|tsx)$/.test(relativeFilePath) &&
    !pageRouteShellPattern.test(source.trim())
  ) {
    violations.push({
      file: relativeFilePath,
      specifier: 'page route files must be thin shells that re-export from @/components/<Domain>/*Entries',
    })
  }

  if (relativeFilePath === 'src/components/Select/financial.js') {
    violations.push({
      file: relativeFilePath,
      specifier: 'financial selects must be exported from components/Financial/SelectEntries',
    })
  }

  if (uiLocalApiFilePattern.test(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'UI-local api file (move request wrappers to src/api/<domain>)',
    })
  }

  if (rootApiFilePattern.test(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'root api file (move request wrappers to src/api/<domain>)',
    })
  }

  if (
    /^src[\\/]components[\\/].*[\\/]api\.(?:js|ts)$/.test(relativeFilePath) &&
    componentApiForwardingShellPattern.test(source.trim())
  ) {
    violations.push({
      file: relativeFilePath,
      specifier: 'component api forwarding shell (import the semantic @/api entry directly)',
    })
  }

  if (
    /^src[\\/]components[\\/].*\.(?:js|jsx|ts|tsx)$/.test(relativeFilePath) &&
    !/(?:Entries|entries)\.js$/.test(relativeFilePath) &&
    componentForwardingShellPattern.test(source.trim())
  ) {
    violations.push({
      file: relativeFilePath,
      specifier: 'component forwarding shell (import the target component directly)',
    })
  }
}
const componentEntryFiles = walk(path.join(srcDir, 'components')).filter((filePath) => {
  const entryPath = normalizeEntryPath(filePath)
  return /(?:Entries|entries)\.js$/.test(filePath) && entryPath.split('/').length === 2
})
const componentEntryImports = new Set()

for (const filePath of componentEntryFiles) {
  const relativeFilePath = path.relative(root, filePath)
  const source = fs.readFileSync(filePath, 'utf8')
  if (!componentEntryReExportOnlyPattern.test(source)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'component entry files must only contain re-export declarations',
    })
  }

  if (componentEntryAbsoluteComponentImportPattern.test(source)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'component entry files must use relative re-export paths',
    })
  }
}

const compatibilityComponentEntries = new Set([
  'Chart/BarChartEntries.js',
  'Chart/LineChartEntries.js',
  'Chart/TooltipEntries.js',
])
const allowedDuplicateComponentEntries = new Set([
  'Financial/FundDetailPageEntries.js|Financial/FundProcessEntries.js',
  'Project/ReviewDetailPageEntries.js|Project/ReviewProcessDetailEntries.js',
])
const removedCompatibilityComponentEntries = new Map([
  ['AfterLease/AdjustEntries.js', 'AfterLease/Adjust*Entries.js'],
  ['AfterLease/CheckPlanListEntries.js', 'AfterLease/CheckPlan*Entries.js'],
  ['AfterLease/PolicyManageEntries.js', 'AfterLease/PolicyManage*Entries.js'],
  ['AfterLease/PolicyManageBaseEntries.js', 'AfterLease/PolicyManageBase*Entries.js'],
  ['AfterLease/CheckPlanStrategyEntries.js', 'AfterLease/CheckPlanStrategy*Entries.js'],
  ['AfterLease/RentCollectionListEntries.js', 'AfterLease/RentCollection*Entries.js'],
  ['Archives/ArchivesEntries.js', 'Archives/* narrow Entries.js'],
  ['BlackGray/AllQueryEntries.js', 'BlackGray/AllQuery*Entries.js'],
  ['BlackGray/BlackGrayEntries.js', 'BlackGray/* narrow Entries.js'],
  ['BlackGray/BreakThroughEntries.js', 'BlackGray/BreakThrough*Entries.js'],
  ['BlackGray/EnterDatabaseEntries.js', 'BlackGray/EnterDatabase*Entries.js'],
  ['BlackGray/OutboundEntries.js', 'BlackGray/Outbound*Entries.js'],
  ['BlackGray/QueryEntries.js', 'BlackGray/AllQuery*Entries.js or BlackGray/QueryIframeEntries.js'],
  ['BlackGray/WarehouseApprovalEntries.js', 'BlackGray/WarehouseApproval*Entries.js'],
  ['BlackGray/WarehouseEntries.js', 'BlackGray/Warehouse*Entries.js'],
  ['BlackGray/WarehouseMainTaskEntries.js', 'BlackGray/WarehouseMainTask*Entries.js'],
  ['Budget/PricingBaseSetEntries.js', 'Budget/PricingBaseSet*Entries.js'],
  ['Budget/PricingBusinessEntries.js', 'Budget/PricingBusiness*Entries.js'],
  ['Budget/PricingFtpInterestEntries.js', 'Budget/PricingFtpInterest*Entries.js'],
  ['Budget/ProjectProfitEntries.js', 'Budget/ProjectProfit*Entries.js'],
  ['Budget/ProvisioningEntries.js', 'Budget/Provisioning*Entries.js'],
  ['BudgetManagement/BudgetManagementEntries.js', 'BudgetManagement/* narrow Entries.js'],
  ['BudgetManagement/PlacementPlanEntries.js', 'BudgetManagement/PlacementPlan*Entries.js'],
  ['BudgetManagement/PlanProfitEntries.js', 'BudgetManagement/PlanProfit*Entries.js'],
  ['BudgetManagement/ProvisionForecastEntries.js', 'BudgetManagement/ProvisionForecast*Entries.js'],
  ['CheckBusiness/CheckBusinessEntries.js', 'BusinessInfoCheck/BusinessInfoCheckEntries.js'],
  ['ClientFileTable/ClientFileTableEntries.js', 'ClientMaterialTable/BusinessMaterialTableEntries.js'],
  ['ClientMaterialTable/ClientMaterialTableEntries.js', 'ClientMaterialTable/BusinessMaterialTableEntries.js'],
  ['Contract/ApplicationDetailLogEntries.js', 'Contract/Application*LogEntries.js'],
  ['Contract/ApplicationDetailEntries.js', 'Contract/ApplicationDetail*Entries.js'],
  ['Contract/MaterialsEntries.js', 'Contract/Contract*Entries.js or Contract/LeaseMaterialsEntries.js'],
  ['Contract/ProcessApprovalDetailEntries.js', 'Contract/*DetailEntries.js'],
  ['Contract/ProcessDetailEntries.js', 'Contract/*DetailEntries.js'],
  ['Contract/ProcessRouteDetailEntries.js', 'Contract/*DetailEntries.js'],
  ['Cpm/ContractCpmEntries.js', 'Cpm/ContractCpm*Entries.js'],
  ['Cpm/CpmEntries.js', 'Cpm/* narrow Entries.js'],
  ['Cpm/MarginManagementEntries.js', 'Cpm/MarginManagement*Entries.js'],
  ['Credit/EstablishEntries.js', 'Credit/Establish*Entries.js'],
  ['Credit/ReviewEntries.js', 'Credit/Review*Entries.js'],
  ['Credit/SearchModalEntries.js', 'Credit/CreditReportSearchEntries.js'],
  ['Customer/MaintainEntries.js', 'Customer/Maintain*Entries.js'],
  ['Dashboard/DashboardEntries.js', 'Dashboard/* narrow Entries.js'],
  ['EvaluationAgency/EvaluationAgencyEntries.js', 'EvaluationAgency/AppraisalAgencyEntries.js'],
  ['FileDiff/FileDiffEntries.js', 'ChangeLogDiff/ChangeLogDiffEntries.js'],
  ['Financial/FundDetailEntries.js', 'Financial/FundDetail*Entries.js'],
  ['Financial/FundDetailRepaymentEntries.js', 'Financial/FundDetail*Entries.js'],
  ['Financial/FundDetailSectionEntries.js', 'Financial/FundDetail*Entries.js'],
  ['Financial/FundListComponentEntries.js', 'Financial/Fund*Entries.js'],
  ['Financial/FundListEntries.js', 'Financial/Fund*Entries.js'],
  ['Financial/FundListPageEntries.js', 'Financial/FundList*Entries.js'],
  ['Financial/LiquidityEntries.js', 'Financial/Liquidity*Entries.js'],
  ['Financial/PaymentDetailEntries.js', 'Financial/PaymentDetailPageEntries.js'],
  ['Financial/PaymentListEntries.js', 'Financial/Payment*Entries.js'],
  ['Report/FinancialReportListEntries.js', 'Report/FinancialReport*Entries.js'],
  ['Kpi/BaseSetEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/BaseSetModalDetailEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/BaseSetOtherConfigEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/BaseSetParameterDetailEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/KpiEstimationEntries.js', 'Kpi/Estimation*Entries.js'],
  ['Kpi/PmAssessEntries.js', 'Kpi/PmAssess*Entries.js'],
  ['Kpi/ProjectAllotListEntries.js', 'Kpi/ProjectAllot*Entries.js'],
  ['Lease/MaintainEntries.js', 'Lease/Maintain*Entries.js'],
  ['LifeCycle/LifeCycleEntries.js', 'LifeCycle/*Entries.js'],
  ['Ocr/OcrEntries.js', 'Ocr/*Entries.js'],
  ['Overdue/OverdueEntries.js', 'Overdue/* narrow Entries.js'],
  ['PaymentApplyColumns/PaymentApplyColumnsEntries.js', 'PaymentFtpColumns/FtpAssessmentColumnsEntries.js'],
  ['PaymentFtpColumns/PaymentFtpColumnsEntries.js', 'PaymentFtpColumns/FtpAssessmentColumnsEntries.js'],
  ['Permission/BifrostEntries.js', 'Permission/* narrow Entries.js'],
  ['Policy/PolicyEntries.js', 'InsurancePolicy/InsurancePolicyEntries.js'],
  ['Process/ComponentEntries.js', 'Process/* narrow Entries.js'],
  ['Process/ProcessEntries.js', 'Process/* narrow Entries.js'],
  ['Project/EstablishmentDetailEntries.js', 'Project/EstablishmentDetail*Entries.js'],
  ['Project/PriceDetailEntries.js', 'Project/PriceDetail*Entries.js'],
  ['Project/ReviewDetailEntries.js', 'Project/ReviewDetail*Entries.js'],
  ['Project/ReviewDetailSectionEntries.js', 'Project/ReviewDetail*Entries.js'],
  ['Project/ReviewMeetingEntries.js', 'Project/ProjectReviewMeetingModalEntries.js'],
  ['Project/ReviewProcessMaterialEntries.js', 'Project/ReviewDetailMaterialEntries.js or Project/ReviewMaterialTableEntries.js'],
  ['Project/ReviewProcessEntries.js', 'Project/ReviewProcess*Entries.js'],
  ['Report/OperationEntries.js', 'Report/Operation*Entries.js'],
  ['Risk/MetricValueEntries.js', 'Risk/MetricValue*Entries.js'],
  ['Risk/RiskStrategyEntries.js', 'Risk/RiskStrategy*Entries.js'],
  ['Risk/SourceCardEntries.js', 'Risk/SourceCard*Entries.js'],
  ['TrackEvent/TrackEventEntries.js', 'TrackEvent/* narrow Entries.js'],
  ['TrackEvent/TrackingEntries.js', 'TrackEvent/* narrow Entries.js'],
  ['WhiteList/WhiteListEntries.js', 'WhiteList/*Entries.js'],
])

for (const filePath of sourceFiles) {
  const source = fs.readFileSync(filePath, 'utf8')
  const relativeFilePath = path.relative(root, filePath)
  const [, sourceComponentDomain] =
    relativeFilePath.match(/^src[\\/]components[\\/]([^\\/]+)/) || []
  for (const { importText, specifier } of extractSpecifiers(source, relativeFilePath)) {
    if (publicStyleImports.has(specifier)) {
      continue
    }

    if (
      /^src[\\/](?:components|pages|layout)[\\/]/.test(relativeFilePath) &&
      specifier.startsWith('.') &&
      relativeApiImportPattern.test(specifier)
    ) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use the semantic @/api/<domain> entry)`,
      })
      continue
    }

    const isComponentImport = specifier.startsWith('@/components/')
    const isPageImport = pageImportPattern.test(specifier)
    const [, sourceApiDomain] = relativeFilePath.match(/^src[\\/]api[\\/]([^\\/]+)/) || []
    const [, legacyApiDomain] = specifier.match(legacyApiImportPattern) || []
    const legacyApiPrefixRule = getLegacyApiPrefixRule(specifier)
    const legacyUtilityPrefixRule = getLegacyUtilityPrefixRule(specifier)
    const [, componentRootImportDomain] = specifier.match(componentRootImportPattern) || []

    const [, targetComponentEntryDomain] = specifier.match(componentEntryPathPattern) || []
    const documentedEntryPath = toDocumentedEntryPath(specifier)
    if (documentedEntryPath) {
      componentEntryImports.add(documentedEntryPath)
    }
    const removedCompatibilityReplacement = removedCompatibilityComponentEntries.get(
      documentedEntryPath
    )

    if (isPageImport) {
      violations.push({
        file: relativeFilePath,
        specifier,
      })
    } else if (legacyApiDomains.has(legacyApiDomain)) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use @/api/${legacyApiDomains.get(legacyApiDomain)} semantic entry)`,
      })
    } else if (sourceApiDomain && legacyApiDomain && sourceApiDomain !== legacyApiDomain) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (src/api domain entries must not import another api domain; declare the semantic endpoint locally)`,
      })
    } else if (apiInterfaceImportPattern.test(specifier) && !relativeFilePath.startsWith('src/api/')) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use the semantic @/api wrapper instead of interface types directly)`,
      })
    } else if (
      legacyUtilityPrefixRule &&
      !legacyUtilityPrefixRule.allowedSourcePathPrefixes?.some((prefix) =>
        relativeFilePath.startsWith(prefix)
      )
    ) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use ${legacyUtilityPrefixRule.replacementPrefix})`,
      })
    } else if (
      legacyApiPrefixRule &&
      !isAllowedLegacyApiPrefixSource(
        legacyApiPrefixRule,
        relativeFilePath,
        sourceComponentDomain
      )
    ) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use ${legacyApiPrefixRule.replacementPrefix} semantic entry)`,
      })
    } else if (stabilizedComponentRootImports.has(componentRootImportDomain)) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use @/components/${stabilizedComponentRootImports.get(componentRootImportDomain)})`,
      })
    } else if (
      componentRootImportDomain &&
      !publicComponentRootImports.has(componentRootImportDomain)
    ) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use an existing public component root or a domain *Entries.js)`,
      })
    } else if (specifier === '@/components') {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use the concrete component entry)`,
      })

      for (const namedImport of extractNamedImports(importText)) {
        if (stableTableRootImports.has(namedImport)) {
          violations.push({
            file: relativeFilePath,
            specifier: `${namedImport} from ${specifier} (use @/components/Table)`,
          })
        } else if (stableSelectRootImports.has(namedImport)) {
          violations.push({
            file: relativeFilePath,
            specifier: `${namedImport} from ${specifier} (use @/components/Select)`,
          })
        } else if (stableDefaultComponentRootImports.has(namedImport)) {
          violations.push({
            file: relativeFilePath,
            specifier: `${namedImport} from ${specifier} (use @/components/${stableDefaultComponentRootImports.get(namedImport)})`,
          })
        }
      }
    } else if (
      removedCompatibilityReplacement &&
      normalizeEntryPath(filePath) !== documentedEntryPath
    ) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (removed compatibility entry; use @/components/${removedCompatibilityReplacement})`,
      })
    } else if (
      isComponentImport &&
      (
      privateComponentPathPattern.test(specifier) ||
      deepComponentPathPattern.test(specifier) ||
      sharedComponentSubpathPattern.test(specifier) ||
      nonEntryComponentSubpathPattern.test(specifier) ||
      (sourceComponentDomain && sourceComponentDomain === targetComponentEntryDomain)
      )
    ) {
      violations.push({
        file: relativeFilePath,
        specifier,
      })
    }
  }
}

const actualComponentEntries = componentEntryFiles.map(normalizeEntryPath).sort()
const actualComponentEntrySet = new Set(actualComponentEntries)
const componentEntryHashes = new Map()
const readme = fs.existsSync(readmePath) ? fs.readFileSync(readmePath, 'utf8') : ''
const documentedComponentEntries = [...readme.matchAll(/^- `([^`]+(?:Entries|entries)\.js)`/gm)]
  .map((match) => match[1])
  .sort()
const documentedComponentEntrySet = new Set(documentedComponentEntries)

for (const filePath of componentEntryFiles) {
  const entryPath = normalizeEntryPath(filePath)
  const content = fs.readFileSync(filePath, 'utf8')
  const entries = componentEntryHashes.get(content) || []
  entries.push(entryPath)
  componentEntryHashes.set(content, entries)
}

for (const entries of componentEntryHashes.values()) {
  if (entries.length <= 1) {
    continue
  }

  const duplicateKey = entries.sort().join('|')
  if (!allowedDuplicateComponentEntries.has(duplicateKey)) {
    violations.push({
      file: entries.map((entryPath) => `src/components/${entryPath}`).join(', '),
      specifier: 'duplicate component entries',
    })
  }
}

for (const entryPath of actualComponentEntries) {
  if (!componentEntryImports.has(entryPath) && !compatibilityComponentEntries.has(entryPath)) {
    violations.push({
      file: `src/components/${entryPath}`,
      specifier: 'unused component entry',
    })
  }

  if (!documentedComponentEntrySet.has(entryPath) && !compatibilityComponentEntries.has(entryPath)) {
    violations.push({
      file: 'README.md',
      specifier: `missing component entry ${entryPath}`,
    })
  }
}

for (const entryPath of documentedComponentEntries) {
  if (!actualComponentEntrySet.has(entryPath)) {
    violations.push({
      file: 'README.md',
      specifier: `stale component entry ${entryPath}`,
    })
  }
}

for (const candidate of findUnusedComponentCandidates({
  includeIndex: true,
  includeEntries: true,
})) {
  violations.push({
    file: candidate,
    specifier: 'unused component candidate',
  })
}

const {
  businessEmbeddingEdges,
  pageAggregationReviewEdges,
} = analyzeUiDomainDeps()
for (const edge of [...businessEmbeddingEdges, ...pageAggregationReviewEdges]) {
  violations.push({
    file: [...edge.files].sort().join(', '),
    specifier: `${edge.sourceScope} -> ${edge.targetScope} requires semantic review`,
  })
}

const componentEntryDepsBaseline = fs.existsSync(componentEntryDepsBaselinePath)
  ? JSON.parse(fs.readFileSync(componentEntryDepsBaselinePath, 'utf8'))
  : null
const componentEntryDepsAnalysis = analyzeComponentEntryDeps()
const unlistedComponentEntryEdges = findUnlistedEdges(
  componentEntryDepsAnalysis.edges,
  componentEntryDepsBaseline
)
const staleComponentEntryBaselineEdges = findStaleBaselineEdges(
  componentEntryDepsAnalysis.edges,
  componentEntryDepsBaseline
)

if (!componentEntryDepsBaseline) {
  violations.push({
    file: path.relative(root, componentEntryDepsBaselinePath),
    specifier: 'missing component entry dependency baseline',
  })
}

for (const edge of unlistedComponentEntryEdges) {
  violations.push({
    file: edge.files.join(', '),
    specifier: `${edge.sourceScope} -> ${edge.target} unlisted cross-domain component entry dependency`,
  })
}

for (const edge of staleComponentEntryBaselineEdges) {
  violations.push({
    file: path.relative(root, componentEntryDepsBaselinePath),
    specifier: `${edge.sourceScope} -> ${edge.target} stale cross-domain component entry dependency baseline entry`,
  })
}

const localSupportFileIncomingImports = new Map(sourceFiles.map((filePath) => [filePath, new Set()]))
const sourceIncomingImports = new Map(sourceFiles.map((filePath) => [filePath, new Set()]))
for (const filePath of sourceFiles) {
  const source = fs.readFileSync(filePath, 'utf8')
  const relativeFilePath = path.relative(root, filePath)
  for (const { specifier } of extractSpecifiers(source, relativeFilePath)) {
    const resolvedImport = resolveSourceImport(filePath, specifier)
    if (resolvedImport && localSupportFileIncomingImports.has(resolvedImport)) {
      localSupportFileIncomingImports.get(resolvedImport).add(filePath)
      sourceIncomingImports.get(resolvedImport).add(filePath)
    }
  }
}

for (const filePath of sourceFiles) {
  const relativeFilePath = path.relative(root, filePath)
  const isLocalSupportFile =
    /^src[\\/](?:components|pages)[\\/].*[\\/](?:api|store|Store)\.(?:js|ts)$/.test(
      relativeFilePath
    )

  if (isLocalSupportFile && localSupportFileIncomingImports.get(filePath)?.size === 0) {
    violations.push({
      file: relativeFilePath,
      specifier: 'unused local support file',
    })
  }
}

for (const filePath of sourceFiles) {
  const relativeFilePath = path.relative(root, filePath)
  const isApiImplementationFile =
    relativeFilePath.startsWith('src/api/') && !relativeFilePath.includes('/interface/')

  if (
    isApiImplementationFile &&
    sourceIncomingImports.get(filePath)?.size === 0 &&
    !allowedZeroIncomingApiFiles.has(relativeFilePath)
  ) {
    violations.push({
      file: relativeFilePath,
      specifier: 'unused api implementation file',
    })
  }
}

for (const filePath of styleFiles) {
  const relativeFilePath = path.relative(root, filePath)
  if (removedLegacyStyleFiles.has(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: `removed legacy style file (use ${removedLegacyStyleFiles.get(relativeFilePath)})`,
    })
  }
}

if (violations.length > 0) {
  console.error('Frontend boundary violations found:')
  for (const violation of violations) {
    console.error(`- ${violation.file}: ${violation.specifier}`)
  }
  process.exit(1)
}

console.log('Frontend boundary check passed.')
