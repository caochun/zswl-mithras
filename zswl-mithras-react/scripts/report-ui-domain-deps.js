const fs = require('fs')
const path = require('path')

const args = new Set(process.argv.slice(2))
const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g
const {
  createDomainAliases,
  createPageSourcePathDomainAliases,
} = require('./domain-report-config')

const domainAliases = createDomainAliases()

const ignoredSourcePathPatterns = [
  /^src[\\/]api[\\/]/,
  /^src[\\/]pages[\\/]demo[\\/]/,
]

const pageSourcePathDomainAliases = createPageSourcePathDomainAliases()

const orchestrationComponentRoots = new Set([
  'Process',
])

const orchestrationTargetScopes = new Set([
  'components/Process',
])

const stableSharedBusinessTargets = new Set([
  'components/AppraisalAgency',
  'components/BlackGrayHit',
  'components/BusinessInfoCheck',
  'components/BusinessMaterialTable',
  'components/ChangeLogDiff',
  'components/ContractBaseInfo',
  'components/ContractLeaseMaterials',
  'components/CreditReportSearch',
  'components/CustomerDebtRatingList',
  'components/CustomerExternalPublicInfo',
  'components/FtpAssessmentColumns',
  'components/FinancialSelect',
  'components/FinancialUrl',
  'components/AfterLeaseLevel5Classify',
  'components/InsurancePolicyColumns',
  'components/InsurancePolicyInfo',
  'components/KpiBaseSetModalDetail',
  'components/LifeCycleCustomer',
  'components/ProcessInfoModal',
  'components/ProcessTaskFlowChart',
  'components/ProjectReviewMeetingModal',
  'components/ProjectReviewSnapshot',
  'components/RentCollectionDetail',
  'components/RentCollectionList',
  'components/RiskPublicMonitorColumns',
  'components/RiskPublicMonitorList',
  'components/RiskSourceCardCalcModal',
  'components/TrackEventModal',
  'components/TrackEventTask',
])

const stablePageAggregationTargets = new Set([
  'components/App',
  'components/Dashboard',
])

const componentAliases = new Map([
  ['CheckBusiness', 'BusinessInfoCheck'],
  ['ClientFileTable', 'ClientMaterialTable'],
  ['FileDiff', 'ChangeLogDiff'],
  ['Policy', 'InsurancePolicy'],
  ['PaymentApplyColumns', 'PaymentFtpColumns'],
])

const componentEntryScopeAliases = new Map([
  ['BlackGray/BlackGrayHitEntries', 'BlackGrayHit'],
  ['AfterLease/AdjustEntries', 'AfterLeaseAdjustDetail'],
  ['AfterLease/CheckPlanCheckListEntries', 'AfterLeaseCheckPlanCheckList'],
  ['AfterLease/CheckPlanCreateEntries', 'AfterLeaseCheckPlanCreate'],
  ['AfterLease/CheckPlanDetailEntries', 'AfterLeaseCheckPlanDetail'],
  ['AfterLease/CheckPlanExternalEntries', 'AfterLeaseExternalCheckDetail'],
  ['AfterLease/CheckPlanListPageEntries', 'AfterLeaseCheckPlanList'],
  ['AfterLease/CheckPlanOpenListEntries', 'AfterLeaseCheckPlanOpenList'],
  ['AfterLease/CheckPlanPrepareEntries', 'AfterLeaseCheckPlanPrepare'],
  ['AfterLease/CheckPlanStrategyEntries', 'AfterLeaseCheckPlanStrategy'],
  ['AfterLease/CheckPlanTemplateEntries', 'AfterLeaseCheckPlanTemplate'],
  ['AfterLease/Level5ClassifyDetailEntries', 'AfterLeaseLevel5ClassifyDetail'],
  ['AfterLease/Level5ClassifyListEntries', 'AfterLeaseLevel5Classify'],
  ['AfterLease/PolicyManageBasePageEntries', 'AfterLeasePolicyManageBasePage'],
  ['AfterLease/PolicyManageBasePolicyEntries', 'AfterLeasePolicyManageBasePolicy'],
  ['AfterLease/PolicyManageBasePolicyToolEntries', 'AfterLeasePolicyManageBasePolicyTool'],
  ['AfterLease/PolicyManageDetailEntries', 'AfterLeasePolicyManageDetail'],
  ['AfterLease/PolicyManageListEntries', 'AfterLeasePolicyManageList'],
  ['AfterLease/PolicyManageRemindEntries', 'AfterLeasePolicyManageRemind'],
  ['AfterLease/RentCollectionDetailEntries', 'RentCollectionDetail'],
  ['AfterLease/RentCollectionListEntries', 'RentCollectionList'],
  ['AfterLease/RentCollectionProcessEntries', 'RentCollectionProcess'],
  ['Budget/AccountsReceivableEntries', 'BudgetAccountsReceivableDetail'],
  ['Budget/ExchangeRateEntries', 'BudgetExchangeRate'],
  ['Budget/PricingBusinessDetailEntries', 'BudgetPricingBusinessDetail'],
  ['Budget/PricingBusinessListEntries', 'BudgetPricingBusinessList'],
  ['Budget/PricingBusinessLogEntries', 'BudgetPricingBusinessLog'],
  ['Budget/PricingFtpInterestDetailEntries', 'BudgetPricingFtpInterestDetail'],
  ['Budget/PricingFtpInterestListEntries', 'BudgetPricingFtpInterestList'],
  ['Budget/PricingFtpInterestPriceChangeEntries', 'BudgetFtpInterestPriceChange'],
  ['Budget/PricingFtpInterestPriceDetailEntries', 'BudgetPricingFtpInterestPriceDetail'],
  ['Budget/ProfitDistributionEntries', 'BudgetProfitDistribution'],
  ['Budget/ProvisioningDataEntries', 'BudgetProvisioningData'],
  ['Budget/ProvisioningImpairmentEntries', 'BudgetProvisioningImpairment'],
  ['Budget/ProvisioningParamsConfigEntries', 'BudgetProvisioningParamsConfig'],
  ['Budget/ProvisioningSharedEntries', 'BudgetProvisioningShared'],
  ['BudgetManagement/AssessmentEntries', 'BudgetManagementAssessment'],
  ['BudgetManagement/ParameterEntries', 'BudgetManagementParameter'],
  ['BudgetManagement/PlacementPlanEntries', 'BudgetManagementPlacementPlan'],
  ['BudgetManagement/PlanCostEntries', 'BudgetManagementPlanCost'],
  ['BudgetManagement/PlanProfitEntries', 'BudgetManagementPlanProfit'],
  ['BudgetManagement/ProvisionForecastEntries', 'BudgetManagementProvisionForecast'],
  ['Archives/ManageEntries', 'ArchivesManage'],
  ['Archives/ManagementEntries', 'ArchivesManagement'],
  ['Archives/OtherFilingMaterialsEntries', 'ArchivesOtherFilingMaterials'],
  ['Archives/TaskEntries', 'ArchivesTask'],
  ['ClientMaterialTable/BusinessMaterialTableEntries', 'BusinessMaterialTable'],
  ['Contract/ChangeDetailEntries', 'ContractChangeDetail'],
  ['Contract/ApplicationDetailLogEntries', 'ContractApplicationDetailLog'],
  ['Contract/ApplicationDetailPageEntries', 'ContractApplicationDetail'],
  ['Contract/BaseInfoEntries', 'ContractBaseInfo'],
  ['Contract/ConfigEntries', 'ContractConfig'],
  ['Contract/ContractMaterialListEntries', 'ContractMaterialList'],
  ['Contract/ContractProtocolEntries', 'ContractProtocol'],
  ['Contract/ContractStartRentMaterialEntries', 'ContractStartRentMaterial'],
  ['Contract/ContractTextEntries', 'ContractText'],
  ['Contract/CreateReceiptDetailEntries', 'ContractCreateReceiptDetail'],
  ['Contract/LeaseMaterialsEntries', 'ContractLeaseMaterials'],
  ['Contract/MarginRefundDetailEntries', 'ContractMarginRefundDetail'],
  ['Contract/ProcessPrepareDetailEntries', 'ContractProcessPrepareDetail'],
  ['Contract/SettlementDetailEntries', 'ContractSettlementDetail'],
  ['Contract/StartRentCheckEntries', 'ContractStartRentCheck'],
  ['Contract/StartRentDetailEntries', 'ContractStartRentDetail'],
  ['Cpm/PaymentWriteOffEntries', 'CpmPaymentWriteOffDetail'],
  ['Cpm/PaymentApplicationDetailEntries', 'CpmPaymentApplicationDetail'],
  ['Cpm/PaymentApplicationMaterialsEntries', 'CpmPaymentApplicationMaterials'],
  ['Cpm/PaymentApplicationPublicCheckEntries', 'CpmPaymentApplicationPublicCheck'],
  ['Cpm/PaymentApplicationPublicInfoEntries', 'CpmPaymentApplicationPublicInfo'],
  ['BlackGray/WarehouseApprovalEntries', 'BlackGrayWarehouseApproval'],
  ['BlackGray/WarehouseMainTaskEntries', 'BlackGrayWarehouseMainTask'],
  ['BlackGray/WarehouseSearchEntries', 'BlackGrayWarehouseSearch'],
  ['BlackGray/WarehouseSubTaskEntries', 'BlackGrayWarehouseSubTask'],
  ['Credit/CreditReportSearchEntries', 'CreditReportSearch'],
  ['Credit/EstablishDetailEntries', 'CreditEstablishDetail'],
  ['Credit/EstablishLogEntries', 'CreditEstablishLog'],
  ['Credit/EstablishPageEntries', 'CreditEstablishPage'],
  ['Credit/ReviewDetailEntries', 'CreditReviewDetail'],
  ['Credit/ReviewLogEntries', 'CreditReviewLog'],
  ['Credit/ReviewPageEntries', 'CreditReviewPage'],
  ['CreditManage/CreditManageEntries', 'CreditManageDetail'],
  ['Customer/ApplyPermissionEntries', 'CustomerApplyPermission'],
  ['Customer/CustomerRatingDetailEntries', 'CustomerRatingDetail'],
  ['Customer/CustomerRatingUploadEntries', 'CustomerRatingUpload'],
  ['Customer/DebtRatingListEntries', 'CustomerDebtRatingList'],
  ['Customer/DebtRatingDetailEntries', 'CustomerDebtRatingDetail'],
  ['Customer/ExternalPublicInfoEntries', 'CustomerExternalPublicInfo'],
  ['Customer/FinancialReportEntries', 'CustomerFinancialReport'],
  ['Customer/HandoverEntries', 'CustomerHandover'],
  ['Customer/MaintainDetailEntries', 'CustomerMaintainDetail'],
  ['Customer/MaintainListEntries', 'CustomerMaintainList'],
  ['Customer/MaintainLogEntries', 'CustomerMaintainLog'],
  ['Customer/SingleViewRiskEntries', 'CustomerSingleViewRisk'],
  ['Dashboard/OverviewEntries', 'Dashboard'],
  ['Dashboard/SsoEntries', 'Dashboard'],
  ['Dashboard/WorkbenchEntries', 'Dashboard'],
  ['EvaluationAgency/AppraisalAgencyEntries', 'AppraisalAgency'],
  ['FilingMaterials/ApplyEntries', 'FilingMaterialsApply'],
  ['FilingMaterials/AfterApplyEntries', 'FilingMaterialsAfterApply'],
  ['FilingMaterials/FundApplyEntries', 'FilingMaterialsFundApply'],
  ['FilingMaterials/OtherApplyEntries', 'FilingMaterialsOtherApply'],
  ['Financial/FundEffectEntries', 'FinancialFundEffect'],
  ['Financial/FundDetailLogEntries', 'FinancialFundDetailLog'],
  ['Financial/FundDetailPageEntries', 'FinancialFundDetailPage'],
  ['Financial/FundDetailAccountEntries', 'FinancialFundDetailAccount'],
  ['Financial/FundDetailAssetEntries', 'FinancialFundDetailAsset'],
  ['Financial/FundDetailRepaymentEntries', 'FinancialFundDetailRepayment'],
  ['Financial/FundActualTableEntries', 'FinancialFundActualTable'],
  ['Financial/FundGuaranteeEntries', 'FinancialFundGuarantee'],
  ['Financial/FundListPageEntries', 'FinancialFundListPage'],
  ['Financial/FundOrgEntries', 'FinancialFundOrg'],
  ['Financial/FundYearRateEntries', 'FinancialFundYearRate'],
  ['Financial/LiquidityAccountBalanceEntries', 'FinancialLiquidityAccountBalance'],
  ['Financial/LiquidityFundDailyReportEntries', 'FinancialLiquidityFundDailyReport'],
  ['Financial/LiquidityManagementEntries', 'FinancialLiquidityManagement'],
  ['Financial/LiquidityPredictionParametersEntries', 'FinancialLiquidityPredictionParameters'],
  ['Financial/LiquiditySupervisionAccountEntries', 'FinancialLiquiditySupervisionAccount'],
  ['Financial/PaymentListPageEntries', 'FinancialPaymentListPage'],
  ['Financial/PaymentLogEntries', 'FinancialPaymentLog'],
  ['Financial/FinancingUrlEntries', 'FinancialUrl'],
  ['Financial/SelectEntries', 'FinancialSelect'],
  ['Financial/DirectDetailEntries', 'FinancialDirectDetail'],
  ['Financial/FinancingCarryInterestEntries', 'FinancialFinancingCarryInterest'],
  ['Financial/FundProcessEntries', 'FinancialFundProcess'],
  ['Financial/PaymentBatchApprovalEntries', 'FinancialPaymentBatchApproval'],
  ['Financial/PaymentDetailPageEntries', 'FinancialPaymentDetail'],
  ['InsurancePolicy/InsurancePolicyEntries', 'InsurancePolicyInfo'],
  ['InsurancePolicy/InsurancePolicyColumnsEntries', 'InsurancePolicyColumns'],
  ['Kpi/BaseSetBaBeiJiTiEntries', 'KpiBaseSetBaBeiJiTi'],
  ['Kpi/BaseSetCopyEntries', 'KpiBaseSetCopy'],
  ['Kpi/BaseSetDepartmentTheoryEntries', 'KpiBaseSetDepartmentTheory'],
  ['Kpi/BaseSetExpenseAccrualEntries', 'KpiBaseSetExpenseAccrual'],
  ['Kpi/BaseSetFormulaTheoryEntries', 'KpiBaseSetFormulaTheory'],
  ['Kpi/BaseSetJinRongShiChangEntries', 'KpiBaseSetJinRongShiChang'],
  ['Kpi/BaseSetListEntries', 'KpiBaseSetList'],
  ['Kpi/BaseSetModalEntries', 'KpiBaseSetModalDetail'],
  ['Kpi/BaseSetParameterDetailEntries', 'KpiBaseSetParameterDetail'],
  ['Kpi/BaseSetParameterModalEntries', 'KpiBaseSetParameterModal'],
  ['Kpi/BaseSetProjectExtractEntries', 'KpiBaseSetProjectExtract'],
  ['Kpi/BaseSetSuiLvWeiHuEntries', 'KpiBaseSetSuiLvWeiHu'],
  ['Kpi/BaseSetTableEntries', 'KpiBaseSetTable'],
  ['Kpi/BaseSetYeWuDeptEntries', 'KpiBaseSetYeWuDept'],
  ['Kpi/BaseSetZhiDengXiShuEntries', 'KpiBaseSetZhiDengXiShu'],
  ['Kpi/BaseSetZhongHouTaiDeptEntries', 'KpiBaseSetZhongHouTaiDept'],
  ['Kpi/PmAssessEntries', 'KpiPmAssess'],
  ['Kpi/ProjectAllotDetailEntries', 'KpiProjectAllotDetail'],
  ['Lease/ApprovalConfirmEntries', 'LeaseApprovalConfirm'],
  ['Lease/MaintainEntries', 'LeaseMaintainDetail'],
  ['LifeCycle/CustomerEntries', 'LifeCycleCustomer'],
  ['LifeCycle/ProjectEntries', 'LifeCycleProject'],
  ['Overdue/CollectionEntries', 'OverdueCollection'],
  ['Overdue/CollectionModalEntries', 'OverdueCollectionModal'],
  ['Overdue/LitigationDocEntries', 'OverdueLitigationDoc'],
  ['Overdue/LitigationRegistrationEntries', 'OverdueLitigationRegistration'],
  ['PaymentFtpColumns/FtpAssessmentColumnsEntries', 'FtpAssessmentColumns'],
  ['Project/EstablishmentDetailLogEntries', 'ProjectEstablishmentDetailLog'],
  ['Project/EstablishmentDetailPageEntries', 'ProjectEstablishmentDetail'],
  ['Project/EstablishmentDetailQuotationEntries', 'ProjectEstablishmentDetailQuotation'],
  ['Project/FinancialReportStatisticsEntries', 'ProjectFinancialReportStatistics'],
  ['Project/FormListItemEntries', 'ProjectFormListItem'],
  ['Project/PriceDetailLogEntries', 'ProjectPriceDetailLog'],
  ['Project/PriceDetailPageEntries', 'ProjectPriceDetail'],
  ['Project/PriceDetailQuotationEntries', 'ProjectPriceDetailQuotation'],
  ['Project/ProjectReviewMeetingModalEntries', 'ProjectReviewMeetingModal'],
  ['Project/ReviewDetailBaseEntries', 'ProjectReviewDetailBase'],
  ['Project/ReviewDetailCashFlowEntries', 'ProjectReviewDetailCashFlow'],
  ['Project/ReviewDetailLogEntries', 'ProjectReviewDetailLog'],
  ['Project/ReviewDetailMaterialEntries', 'ProjectReviewDetailMaterial'],
  ['Project/ReviewDetailPageEntries', 'ProjectReviewDetailPage'],
  ['Project/ReviewDetailQuotationEntries', 'ProjectReviewDetailQuotation'],
  ['Project/ReviewProcessDetailEntries', 'ProjectReviewProcessDetail'],
  ['Project/ReviewProcessMaterialEntries', 'ProjectReviewProcessMaterial'],
  ['Project/ReviewProcessMeetingEntries', 'ProjectReviewProcessMeeting'],
  ['Project/ReviewSnapshotEntries', 'ProjectReviewSnapshot'],
  ['Risk/PublicMonitorDetailEntries', 'RiskPublicMonitorDetail'],
  ['Process/ApprovalHistoryEntries', 'ProcessApprovalHistory'],
  ['Process/BlankBlockEntries', 'ProcessBlankBlock'],
  ['Process/ProcessInfoModalEntries', 'ProcessInfoModal'],
  ['Process/ProcessSnapshotEntries', 'ProcessSnapshot'],
  ['Process/ProcessTaskFlowChartEntries', 'ProcessTaskFlowChart'],
  ['Process/ProcessTypeTreeEntries', 'ProcessTypeTree'],
  ['Report/FinancialReportApprovalEntries', 'FinancialReportApproval'],
  ['Risk/OverdueEntries', 'RiskOverdueListSearch'],
  ['Risk/PublicMonitorColumnsEntries', 'RiskPublicMonitorColumns'],
  ['Risk/PublicMonitorListEntries', 'RiskPublicMonitorList'],
  ['Risk/RiskStrategyConcentrationEntries', 'RiskStrategyConcentration'],
  ['Risk/RiskStrategyIndicatorEntries', 'RiskStrategyIndicator'],
  ['Risk/RiskStrategyPageEntries', 'RiskStrategyPage'],
  ['Risk/RiskStrategyRelateMonitorEntries', 'RiskStrategyRelateMonitor'],
  ['Risk/SourceCardEntries', 'RiskSourceCardCalcModal'],
  ['TrackEvent/TrackEventDetailEntries', 'TrackEventDetail'],
  ['TrackEvent/TrackEventListEntries', 'TrackEventList'],
  ['TrackEvent/TrackEventModalEntries', 'TrackEventModal'],
  ['TrackEvent/TrackEventTaskEntries', 'TrackEventTask'],
])

const publicComponentRoots = new Set([
  'Actions',
  'Amount',
  'AmountRange',
  'Chart',
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

function normalizeDomain(domain) {
  return domainAliases.get(domain) || domain
}

function normalizeComponentDomain(domain) {
  return componentAliases.get(domain) || domain
}

function normalizeComponentTarget(specifier, componentDomain) {
  const [, componentEntryPath] =
    specifier.match(/^@\/components\/([^/'"]+\/[^/'"]*(?:Entries|entries)(?:\.js)?)$/) || []
  const normalizedEntryPath = componentEntryPath?.replace(/\.js$/, '')
  return componentEntryScopeAliases.get(normalizedEntryPath) || normalizeComponentDomain(componentDomain)
}

function walk(dir, files = []) {
  if (!fs.existsSync(dir)) {
    return files
  }

  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const filePath = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      walk(filePath, files)
    } else if (sourceFilePattern.test(entry.name)) {
      files.push(filePath)
    }
  }

  return files
}

function getSourceScope(relativeFilePath) {
  if (ignoredSourcePathPatterns.some((pattern) => pattern.test(relativeFilePath))) {
    return null
  }

  const [, utilityDomain] =
    relativeFilePath.match(/^src[\\/]utils[\\/]domains[\\/]([^\\/]+)/) || []
  if (utilityDomain) {
    return {
      key: `utils/domains/${utilityDomain}`,
      domain: normalizeDomain(utilityDomain),
      kind: 'utils/domains',
    }
  }

  const pagePathAlias = pageSourcePathDomainAliases.find(({ pattern }) =>
    pattern.test(relativeFilePath)
  )
  if (pagePathAlias) {
    return {
      key: pagePathAlias.key,
      domain: normalizeDomain(pagePathAlias.domain),
      kind: 'pages',
    }
  }

  const [, componentDomain] = relativeFilePath.match(/^src[\\/]components[\\/]([^\\/]+)/) || []
  if (componentDomain) {
    const normalizedComponentDomain = normalizeComponentDomain(componentDomain)
    return {
      key: `components/${normalizedComponentDomain}`,
      domain: normalizeDomain(normalizedComponentDomain),
      kind: 'components',
    }
  }

  const [, pageDomain] = relativeFilePath.match(/^src[\\/]pages[\\/]([^\\/]+)/) || []
  if (pageDomain) {
    return {
      key: `pages/${pageDomain}`,
      domain: normalizeDomain(pageDomain),
      kind: 'pages',
    }
  }

  return null
}

function getTargetScope(specifier) {
  const [, componentDomain] = specifier.match(/^@\/components\/([^/'"]+)/) || []
  if (componentDomain) {
    const normalizedComponentDomain = normalizeComponentDomain(componentDomain)
    // Display stable entry capabilities in the report, but keep the original component
    // root as the domain used to filter same-domain page shells.
    const normalizedComponentTarget = normalizeComponentTarget(specifier, componentDomain)
    if (publicComponentRoots.has(normalizedComponentTarget)) {
      return null
    }

    return {
      key: `components/${normalizedComponentTarget}`,
      domain: normalizeDomain(normalizedComponentDomain),
      kind: 'components',
    }
  }

  const [, pageDomain] = specifier.match(/^@\/pages\/([^/'"]+)/) || []
  if (pageDomain) {
    return {
      key: `pages/${pageDomain}`,
      domain: normalizeDomain(pageDomain),
      kind: 'pages',
    }
  }

  const [, utilityDomain] = specifier.match(/^@\/utils\/domains\/([^/'"]+)/) || []
  if (utilityDomain) {
    return {
      key: `utils/domains/${utilityDomain}`,
      domain: normalizeDomain(utilityDomain),
      kind: 'utils/domains',
    }
  }

  return null
}

function analyzeUiDomainDeps() {
  const edges = new Map()

  for (const filePath of walk(srcDir)) {
    const source = fs.readFileSync(filePath, 'utf8')
    const relativeFilePath = path.relative(root, filePath).split(path.sep).join('/')
    const sourceScope = getSourceScope(relativeFilePath)

    if (!sourceScope) {
      continue
    }

    let match
    while ((match = importPattern.exec(source))) {
      const specifier = match[1]
      const targetScope = getTargetScope(specifier)

      if (!targetScope || targetScope.domain.toLowerCase() === sourceScope.domain.toLowerCase()) {
        continue
      }

      const edgeKey = `${sourceScope.key} -> ${targetScope.key}`
      const edge = edges.get(edgeKey) || {
        sourceScope: sourceScope.key,
        targetScope: targetScope.key,
        files: new Set(),
        specifiers: new Set(),
      }

      edge.files.add(relativeFilePath)
      edge.specifiers.add(specifier)
      edges.set(edgeKey, edge)

    }
  }

  const sortedEdges = [...edges.values()].sort((a, b) => {
    const fileCountCompare = b.files.size - a.files.size
    if (fileCountCompare !== 0) {
      return fileCountCompare
    }

    const sourceCompare = a.sourceScope.localeCompare(b.sourceScope)
    if (sourceCompare !== 0) {
      return sourceCompare
    }

    return a.targetScope.localeCompare(b.targetScope)
  })

  const workflowOrchestrationEdges = sortedEdges.filter(isWorkflowOrchestrationEdge)
  const pageAggregationEdges = sortedEdges.filter(
    (edge) =>
      edge.sourceScope.startsWith('pages/') && !isWorkflowOrchestrationEdge(edge)
  )
  const pageStableSharedBusinessEdges = pageAggregationEdges.filter((edge) =>
    stableSharedBusinessTargets.has(edge.targetScope) ||
    stablePageAggregationTargets.has(edge.targetScope)
  )
  const pageAggregationReviewEdges = pageAggregationEdges.filter(
    (edge) =>
      !stableSharedBusinessTargets.has(edge.targetScope) &&
      !stablePageAggregationTargets.has(edge.targetScope)
  )
  const domainImplementationEdges = sortedEdges.filter(
    (edge) =>
      !workflowOrchestrationEdges.includes(edge) &&
      !pageAggregationEdges.includes(edge)
  )
  const stableSharedBusinessEdges = domainImplementationEdges.filter((edge) =>
    stableSharedBusinessTargets.has(edge.targetScope)
  )
  const businessEmbeddingEdges = domainImplementationEdges.filter(
    (edge) => !stableSharedBusinessTargets.has(edge.targetScope)
  )

  return {
    sortedEdges,
    workflowOrchestrationEdges,
    pageStableSharedBusinessEdges,
    pageAggregationReviewEdges,
    stableSharedBusinessEdges,
    businessEmbeddingEdges,
  }
}

function isWorkflowOrchestrationEdge(edge) {
  return (
    edge.sourceScope === 'pages/process' ||
    [...orchestrationComponentRoots].some((root) => edge.sourceScope === `components/${root}`) ||
    orchestrationTargetScopes.has(edge.targetScope)
  )
}

function printEdges(title, edgesToPrint) {
  console.log(title)

  if (edgesToPrint.length === 0) {
    console.log('- none')
    return
  }

  for (const edge of edgesToPrint) {
    console.log(`- ${edge.sourceScope} -> ${edge.targetScope}: ${edge.files.size} file(s)`)
    for (const specifier of [...edge.specifiers].sort()) {
      console.log(`  ${specifier}`)
    }
  }
}

function printFanIn(title, edgesToPrint) {
  const targetFanIn = new Map()

  for (const edge of edgesToPrint) {
    const fanIn = targetFanIn.get(edge.targetScope) || new Set()
    fanIn.add(edge.sourceScope)
    targetFanIn.set(edge.targetScope, fanIn)
  }

  console.log(title)

  if (targetFanIn.size === 0) {
    console.log('- none')
    return
  }

  for (const [target, sources] of [...targetFanIn.entries()].sort(([a], [b]) =>
    a.localeCompare(b)
  )) {
    console.log(`- ${target}: ${sources.size} scope(s) [${[...sources].sort().join(', ')}]`)
  }
}

function runCli() {
  const {
    sortedEdges,
    workflowOrchestrationEdges,
    pageStableSharedBusinessEdges,
    pageAggregationReviewEdges,
    stableSharedBusinessEdges,
    businessEmbeddingEdges,
  } = analyzeUiDomainDeps()

  if (args.has('--fail-on-review') && (
    businessEmbeddingEdges.length > 0 ||
    pageAggregationReviewEdges.length > 0
  )) {
    printEdges(
      'Cross-domain UI dependencies from domain implementation code that still need semantic review:',
      businessEmbeddingEdges
    )
    console.log('')
    printEdges(
      'Cross-domain UI dependencies from page aggregation code that still need semantic review:',
      pageAggregationReviewEdges
    )
    process.exit(1)
  }
  if (args.has('--fail-on-review')) {
    console.log('No unreviewed cross-domain UI dependencies found.')
    return
  }

  if (sortedEdges.length === 0) {
    console.log('No cross-domain UI dependencies found in src.')
    return
  }

  printEdges(
    'Stable shared business capabilities used by domain implementation code:',
    stableSharedBusinessEdges
  )

  console.log('')
  printEdges(
    'Cross-domain UI dependencies from domain implementation code that still need semantic review:',
    businessEmbeddingEdges
  )

  console.log('')
  printEdges('Cross-domain UI dependencies from workflow orchestration code:', workflowOrchestrationEdges)

  console.log('')
  printEdges(
    'Stable shared business capabilities used by page aggregation code:',
    pageStableSharedBusinessEdges
  )

  console.log('')
  printEdges(
    'Cross-domain UI dependencies from page aggregation code that still need semantic review:',
    pageAggregationReviewEdges
  )

  console.log('')
  printFanIn(
    'Stable shared business capability fan-in from domain implementation code:',
    stableSharedBusinessEdges
  )

  console.log('')
  printFanIn(
    'Semantic-review target fan-in from domain implementation code:',
    businessEmbeddingEdges
  )

  console.log('')
  printFanIn('UI target fan-in from workflow orchestration code:', workflowOrchestrationEdges)

  console.log('')
  printFanIn(
    'Stable shared business capability fan-in from page aggregation code:',
    pageStableSharedBusinessEdges
  )

  console.log('')
  printFanIn(
    'Semantic-review target fan-in from page aggregation code:',
    pageAggregationReviewEdges
  )
}

if (require.main === module) {
  runCli()
}

module.exports = {
  analyzeUiDomainDeps,
}
