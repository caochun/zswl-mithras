const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const readmePath = path.join(root, 'README.md')
const scanDirs = [srcDir]
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g

const privateComponentPathPattern = /^@\/components\/[^'"]+\/(?:api|store|context|config|Config|Column|columns)(?:\.js)?$/
const deepComponentPathPattern = /^@\/components\/[^'"]+\/[^'"]+\/[^'"]+\/[^'"]+/
const sharedComponentSubpathPattern =
  /^@\/components\/(?:Actions|Form|Format|Table)\/[^'"]+|^@\/components\/BreadLine\/config$|^@\/components\/Chart\/tooltip$/
const nonEntryComponentSubpathPattern =
  /^@\/components\/[^/'"]+\/(?![^/'"]*(?:Entries|entries)(?:\.js)?$)[^/'"]+(?:\.js)?$/
const componentEntryPathPattern =
  /^@\/components\/([^/'"]+)\/[^/'"]*(?:Entries|entries)(?:\.js)?$/
const stabilizedComponentRootImports = new Map([
  ['BlackInfo', 'BlackInfo/BlackInfoEntries'],
  ['ClientFileTable', 'ClientFileTable/ClientFileTableEntries'],
  ['Dashboard', 'Dashboard/DashboardEntries'],
  ['EvaluationAgency', 'EvaluationAgency/EvaluationAgencyEntries'],
  ['FileDiff', 'FileDiff/FileDiffEntries'],
  ['JumpClient', 'JumpClient/JumpClientEntries'],
  ['PaymentApplyColumns', 'PaymentApplyColumns/PaymentApplyColumnsEntries'],
  ['Policy', 'Policy/PolicyEntries'],
  ['PolicyColumns', 'PolicyColumns/PolicyColumnsEntries'],
  ['RiskActions', 'BlackGray/BlackGrayEntries or Actions'],
  ['UpdateRatingInfoButton', 'UpdateRatingInfoButton/UpdateRatingInfoButtonEntries'],
  ['ZhongDengButton', 'ZhongDengButton/ZhongDengButtonEntries'],
])
const componentRootImportPattern = /^@\/components\/([^/'"]+)$/
const pageImportPattern = /^@\/pages\//
const legacyUtilityPrefixRules = [
  {
    legacyPrefix: '@/utils/afterLease',
    replacementPrefix: '@/utils/domains/afterLease/AfterLeaseUtils',
    allowedSourcePathPrefixes: ['src/utils/domains/afterLease/AfterLeaseUtils.js'],
  },
  {
    legacyPrefix: '@/utils/budgetManagement',
    replacementPrefix: '@/utils/domains/budgetManagement/BudgetManagementUtils',
    allowedSourcePathPrefixes: ['src/utils/domains/budgetManagement/BudgetManagementUtils.js'],
  },
  {
    legacyPrefix: '@/utils/customer',
    replacementPrefix: '@/utils/domains/customer/CustomerUtils',
    allowedSourcePathPrefixes: ['src/utils/domains/customer/CustomerUtils.js'],
  },
  {
    legacyPrefix: '@/utils/customerRat',
    replacementPrefix: '@/utils/domains/customer/CustomerRatUtils',
    allowedSourcePathPrefixes: ['src/utils/customerRat.js'],
  },
  {
    legacyPrefix: '@/utils/dashboard',
    replacementPrefix: '@/utils/domains/dashboard/DashboardUtils*',
    allowedSourcePathPrefixes: [
      'src/utils/dashboard.js',
      'src/utils/dashboardColumns.js',
      'src/utils/dashboardFilterKeys.js',
      'src/utils/dashboardOperation.js',
    ],
  },
  {
    legacyPrefix: '@/utils/kpi',
    replacementPrefix: '@/utils/domains/kpi/KpiUtils',
    allowedSourcePathPrefixes: ['src/utils/domains/kpi/KpiUtils.js'],
  },
  {
    legacyPrefix: '@/utils/paymentApplication',
    replacementPrefix: '@/utils/domains/cpm/PaymentApplicationUtils',
    allowedSourcePathPrefixes: ['src/utils/paymentApplication.js'],
  },
  {
    legacyPrefix: '@/utils/processFlow',
    replacementPrefix: '@/utils/domains/process/ProcessFlowContext',
    allowedSourcePathPrefixes: ['src/utils/domains/process/ProcessFlowContext.js'],
  },
  {
    legacyPrefix: '@/utils/report',
    replacementPrefix: '@/utils/domains/report/ReportUtils',
    allowedSourcePathPrefixes: ['src/utils/domains/report/ReportUtils.js'],
  },
  {
    legacyPrefix: '@/utils/risk',
    replacementPrefix: '@/utils/domains/risk/RiskUtils',
    allowedSourcePathPrefixes: ['src/utils/domains/risk/RiskUtils.js'],
  },
  {
    legacyPrefix: '@/utils/rzyConfig',
    replacementPrefix: '@/utils/domains/rzy/RzyConfig',
    allowedSourcePathPrefixes: ['src/utils/domains/rzy/RzyConfig.js'],
  },
  {
    legacyPrefix: '@/utils/hooks/useGetStatus',
    replacementPrefix: '@/utils/domains/blackGray/BlackGrayStatusUtils',
    allowedSourcePathPrefixes: ['src/utils/domains/blackGray/BlackGrayStatusUtils.js'],
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
      'src/api/lease/evaluationAgencyMaintainApi.ts',
      'src/api/whiteList/appraisalCompanyApi.ts',
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
      'src/api/evaluationAgency/assessmentWhitelistApi.ts',
      'src/pages/whiteList/',
    ],
  },
  {
    legacyPrefix: '@/api/ocr/ocrInvoiceApi',
    replacementPrefix: '@/api/lease/vatInvoiceApi',
    allowedSourcePathPrefixes: [
      'src/api/lease/vatInvoiceApi.ts',
      'src/pages/ocr/',
    ],
  },
  {
    legacyPrefix: '@/api/process/flowExecution',
    replacementPrefix: '@/api/customer/customerRat/customerRatApprovalApi',
    allowedSourcePathPrefixes: [
      'src/api/customer/customerRat/customerRatApprovalApi.js',
      'src/pages/process/',
    ],
  },
  {
    legacyPrefix: '@/api/contract/baseInfo',
    replacementPrefix:
      '@/api/budget/contractInfoApi, @/api/trackEvent/contractInfoApi, or @/api/contract/baseInfo in Contract domain',
    allowedSourceDomains: ['Contract'],
    allowedSourcePathPrefixes: [
      'src/api/budget/contractInfoApi.js',
      'src/api/trackEvent/contractInfoApi.js',
      'src/pages/contract/',
    ],
  },
  {
    legacyPrefix: '@/api/contract/contractDetail',
    replacementPrefix:
      '@/api/process/detail/contractDetailApi or @/api/contract/contractDetail in Contract domain',
    allowedSourceDomains: ['Contract'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/contractDetailApi.js',
      'src/pages/contract/',
    ],
  },
  {
    legacyPrefix: '@/api/financial/fundApi',
    replacementPrefix: '@/api/contract/lprApi or @/api/financial/fundApi in Financial domain',
    allowedSourceDomains: ['Financial'],
    allowedSourcePathPrefixes: [
      'src/api/contract/lprApi.js',
      'src/pages/financial/',
    ],
  },
  {
    legacyPrefix: '@/api/filingMaterials/otherFilingMaterialsDetail',
    replacementPrefix:
      '@/api/process/detail/filingMaterialsApi or @/api/filingMaterials/otherFilingMaterialsDetail in FilingMaterials domain',
    allowedSourceDomains: ['FilingMaterials'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/filingMaterialsApi.js',
      'src/pages/fillingMaterialsDetail/',
    ],
  },
  {
    legacyPrefix: '@/api/approval/processModifyRemarkApi',
    replacementPrefix:
      '@/api/<domain>/approvalRemarkApi or @/api/common/approvalRemarkApi for shared approval components',
    allowedSourcePathPrefixes: [
      'src/api/common/approvalRemarkApi.ts',
      'src/api/contract/approvalRemarkApi.js',
      'src/api/credit/approvalRemarkApi.js',
      'src/api/financial/approvalRemarkApi.js',
      'src/api/project/approvalRemarkApi.js',
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
    legacyPrefix: '@/api/budget/pricing/ftpInterestChangeApi',
    replacementPrefix:
      '@/api/process/detail/ftpInterestChangeApi or @/api/budget/pricing/ftpInterestChangeApi in Budget domain',
    allowedSourceDomains: ['Budget', 'budget'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/ftpInterestChangeApi.ts',
      'src/pages/budget/',
    ],
  },
  {
    legacyPrefix: '@/api/common/workbenchApi',
    replacementPrefix: '@/api/common/userCustomConfigApi or @/api/dashboard/feikongSsoApi',
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
      'src/api/dashboard/workbenchMessageApi.js',
      'src/api/layout/messageApi.js',
      'src/pages/msgNotification/',
    ],
  },
  {
    legacyPrefix: '@/api/permission/login',
    replacementPrefix: '@/api/layout/fastLoginApi or @/api/permission/login in Login page',
    allowedSourcePathPrefixes: [
      'src/api/layout/fastLoginApi.js',
      'src/pages/login/',
    ],
  },
  {
    legacyPrefix: '@/api/customer/customerOverview',
    replacementPrefix: '@/api/customerView/customerOverviewApi or @/api/customer/customerOverview in Customer domain',
    allowedSourcePathPrefixes: [
      'src/api/customerView/customerOverviewApi.js',
      'src/pages/customer/',
    ],
  },
  {
    legacyPrefix: '@/api/blackGray/queryExternalDataApi',
    replacementPrefix: '@/api/customerView/blackGrayApi or @/api/blackGray/queryExternalDataApi in BlackGray domain',
    allowedSourceDomains: ['BlackGray', 'BlackInfo'],
    allowedSourcePathPrefixes: [
      'src/api/customerView/blackGrayApi.js',
      'src/pages/blackListManage/',
    ],
  },
  {
    legacyPrefix: '@/api/risk/customerUnifiedViewController',
    replacementPrefix: '@/api/customerView/riskAreaApi or @/api/risk/customerUnifiedViewController in Risk domain',
    allowedSourcePathPrefixes: [
      'src/api/customerView/riskAreaApi.js',
      'src/pages/risk/',
    ],
  },
  {
    legacyPrefix: '@/api/risk/monitorEarly',
    replacementPrefix: '@/api/customerView/riskWarningApi or @/api/risk/monitorEarly in Risk monitor pages',
    allowedSourcePathPrefixes: [
      'src/api/customerView/riskWarningApi.js',
      'src/pages/monitorEarly/',
    ],
  },
  {
    legacyPrefix: '@/api/risk/publicMonitor',
    replacementPrefix:
      '@/api/lifeCycle/riskWarningApi, @/api/process/detail/publicMonitorApi, or @/api/risk/publicMonitor in Risk domain',
    allowedSourceDomains: ['Risk'],
    allowedSourcePathPrefixes: [
      'src/api/lifeCycle/riskWarningApi.js',
      'src/api/process/detail/publicMonitorApi.js',
      'src/pages/risk/',
    ],
  },
  {
    legacyPrefix: '@/api/overdue/collectionManagementApi',
    replacementPrefix:
      '@/api/process/detail/overdueCollectionApi or @/api/overdue/collectionManagementApi in Overdue domain',
    allowedSourceDomains: ['Overdue'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/overdueCollectionApi.js',
      'src/pages/overdue/',
    ],
  },
  {
    legacyPrefix: '@/api/overdue/sealForDocumentsApi',
    replacementPrefix:
      '@/api/process/detail/overdueSealDocumentApi or @/api/overdue/sealForDocumentsApi in Overdue domain',
    allowedSourceDomains: ['Overdue'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/overdueSealDocumentApi.js',
      'src/pages/overdue/',
    ],
  },
  {
    legacyPrefix: '@/api/cpm/payment/paymentApplicationDetail',
    replacementPrefix:
      '@/api/process/detail/paymentApplicationDetailApi, @/api/process/operation/paymentOperationApi, or @/api/cpm/payment/paymentApplicationDetail in Cpm domain',
    allowedSourceDomains: ['Cpm', 'cpm'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/paymentApplicationDetailApi.js',
      'src/api/process/operation/paymentOperationApi.js',
      'src/utils/domains/cpm/PaymentApplicationUtils.js',
      'src/pages/cpm/',
      'src/pages/process/Detail/ZTabs/Operation/',
    ],
  },
  {
    legacyPrefix: '@/api/cpm/payment/publicInfoApi_edited',
    replacementPrefix:
      '@/api/process/operation/paymentPublicInfoApi or @/api/cpm/payment/publicInfoApi_edited in Cpm domain',
    allowedSourceDomains: ['Cpm', 'cpm'],
    allowedSourcePathPrefixes: [
      'src/api/process/operation/paymentPublicInfoApi.ts',
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
      'src/api/process/detail/projectReviewDetailApi.js',
      'src/pages/project/',
    ],
  },
  {
    legacyPrefix: '@/api/project/projReviewFinancialReport',
    replacementPrefix:
      '@/api/process/operation/projectReviewFinancialReportApi or @/api/project/projReviewFinancialReport in Project domain',
    allowedSourceDomains: ['Project'],
    allowedSourcePathPrefixes: [
      'src/api/process/operation/projectReviewFinancialReportApi.js',
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
      'src/api/process/detail/projectReviewMeetingMinuteApi.js',
      'src/pages/project/',
    ],
  },
  {
    legacyPrefix: '@/api/customer/customerRat/debtRatApi',
    replacementPrefix:
      '@/api/process/detail/debtRatingApi or @/api/customer/customerRat/debtRatApi in Customer/process utilities',
    allowedSourceDomains: ['Customer'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/debtRatingApi.js',
      'src/pages/customer/',
      'src/pages/process/Detail/ZTabs/Operation/Components/Operator/',
    ],
  },
  {
    legacyPrefix: '@/api/customer/customerRat/customerRatApi',
    replacementPrefix:
      '@/api/project/ratingApi, @/api/process/detail/customerRatingApi, or @/api/customer/customerRat/customerRatApi in Customer/process utilities',
    allowedSourceDomains: ['Customer'],
    allowedSourcePathPrefixes: [
      'src/api/process/detail/customerRatingApi.js',
      'src/api/process/operation/customerRatingOperationApi.js',
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
      'src/api/process/application/customerMaintainApi.js',
      'src/components/Customer/',
      'src/pages/customer/',
    ],
  },
  {
    legacyPrefix: '@/api/budget/flowCenter/bankFlowProcessingCenterApi',
    replacementPrefix: '@/api/cpm/payment/writeOffFlowCenterApi',
    allowedSourceDomains: ['Budget'],
    allowedSourcePathPrefixes: [
      'src/api/cpm/payment/writeOffFlowCenterApi.js',
      'src/pages/budget/flowCenter/',
    ],
  },
  {
    legacyPrefix: '@/api/budget/flowCenter/flowCenterApi',
    replacementPrefix: '@/api/cpm/payment/writeOffFlowCenterApi',
    allowedSourceDomains: ['Budget'],
    allowedSourcePathPrefixes: [
      'src/api/cpm/payment/writeOffFlowCenterApi.js',
      'src/pages/budget/flowCenter/',
    ],
  },
]
const legacyApiImportPattern = /^@\/api\/([^/'"]+)(?:\/|$)/

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
    } else if (sourceFilePattern.test(entry.name)) {
      files.push(filePath)
    }
  }

  return files
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

const violations = []
const componentEntryFiles = walk(path.join(srcDir, 'components')).filter((filePath) => {
  const entryPath = normalizeEntryPath(filePath)
  return /(?:Entries|entries)\.js$/.test(filePath) && entryPath.split('/').length === 2
})
const componentEntryImports = new Set()
const compatibilityComponentEntries = new Set([
  'Chart/BarChartEntries.js',
  'Chart/LineChartEntries.js',
  'Chart/TooltipEntries.js',
])

for (const filePath of scanDirs.flatMap((dir) => walk(dir))) {
  const source = fs.readFileSync(filePath, 'utf8')
  const relativeFilePath = path.relative(root, filePath)
  const [, sourceComponentDomain] =
    relativeFilePath.match(/^src[\\/]components[\\/]([^\\/]+)/) || []
  let match
  while ((match = importPattern.exec(source))) {
    const specifier = match[1]
    const isComponentImport = specifier.startsWith('@/components/')
    const isPageImport = pageImportPattern.test(specifier)
    const [, legacyApiDomain] = specifier.match(legacyApiImportPattern) || []
    const legacyApiPrefixRule = getLegacyApiPrefixRule(specifier)
    const legacyUtilityPrefixRule = getLegacyUtilityPrefixRule(specifier)
    const [, componentRootImportDomain] = specifier.match(componentRootImportPattern) || []

    const [, targetComponentEntryDomain] = specifier.match(componentEntryPathPattern) || []
    const documentedEntryPath = toDocumentedEntryPath(specifier)
    if (documentedEntryPath) {
      componentEntryImports.add(documentedEntryPath)
    }

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
    } else if (
      legacyUtilityPrefixRule &&
      !legacyUtilityPrefixRule.allowedSourcePathPrefixes.some((prefix) =>
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
const readme = fs.existsSync(readmePath) ? fs.readFileSync(readmePath, 'utf8') : ''
const documentedComponentEntries = [...readme.matchAll(/^- `([^`]+(?:Entries|entries)\.js)`/gm)]
  .map((match) => match[1])
  .sort()
const documentedComponentEntrySet = new Set(documentedComponentEntries)

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

if (violations.length > 0) {
  console.error('Frontend boundary violations found:')
  for (const violation of violations) {
    console.error(`- ${violation.file}: ${violation.specifier}`)
  }
  process.exit(1)
}

console.log('Frontend boundary check passed.')
