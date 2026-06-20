const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const readmePath = path.join(root, 'README.md')
const { findUnusedComponentCandidates } = require('./report-unused-component-candidates')
const { analyzeUiDomainDeps } = require('./report-ui-domain-deps')
const scanDirs = [srcDir]
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const scannableFilePattern = /\.(js|jsx|ts|tsx|less)$/
const copiedSourceFilePattern =
  /(?:^|[\\/])(?:copy|backup|bak)[\\/]|(?:^|[\\/])[^\\/]*(?: copy|副本|备份|backup|bak)\.(?:js|jsx|ts|tsx)$/i
const sourceExtensions = ['.js', '.jsx', '.ts', '.tsx']
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g
const styleImportPattern = /@import\s+(?:\([^)]*\)\s*)?['"]~?([^'"]+)['"]/g
const componentApiForwardingShellPattern =
  /^export\s+\{\s*default\s*\}\s+from\s+['"]@\/api\/[^'"]+['"]\s*;?\s*$/
const pageRouteShellPattern =
  /^export\s+\{[\s\S]*\}\s+from\s+['"]@\/components\/[^'"]+['"]\s*;?\s*$/
const uiLocalApiFilePattern =
  /^src[\\/](?:components|pages|layout)[\\/].*[\\/]api\.(?:js|jsx|ts|tsx)$/
const rootApiFilePattern = /^src[\\/]api[\\/][^\\/]+\.(?:js|jsx|ts|tsx)$/
const relativeApiImportPattern =
  /^\.{1,2}(?:\/[^'"]*)?\/api(?:\.(?:js|jsx|ts|tsx)|\/index(?:\.(?:js|jsx|ts|tsx))?)?$/

const privateComponentPathPattern = /^@\/components\/[^'"]+\/(?:api|store|context|config|Config|Column|columns)(?:\.js)?$/
const deepComponentPathPattern = /^@\/components\/[^'"]+\/[^'"]+\/[^'"]+\/[^'"]+/
const sharedComponentSubpathPattern =
  /^@\/components\/(?:Actions|Form|Format|Table)\/[^'"]+|^@\/components\/Chart\/tooltip$/
const nonEntryComponentSubpathPattern =
  /^@\/components\/[^/'"]+\/(?![^/'"]*(?:Entries|entries)(?:\.js)?$)[^/'"]+(?:\.js)?$/
const componentEntryPathPattern =
  /^@\/components\/([^/'"]+)\/[^/'"]*(?:Entries|entries)(?:\.js)?$/
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
const publicStyleImports = new Set(['@/components/commonLess/animation.less'])
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
    legacyPrefix: '@/utils/hooks/useGetStatus',
    replacementPrefix: '@/utils/domains/blackGray/BlackGrayStatusUtils',
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
    replacementPrefix: '@/api/customerView/riskAreaApi or @/api/risk/customerUnifiedViewController in Risk domain',
    allowedSourcePathPrefixes: [
      'src/pages/risk/',
    ],
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

const violations = []
const sourceFiles = scanDirs.flatMap((dir) => walk(dir))
for (const filePath of sourceFiles) {
  const relativeFilePath = path.relative(root, filePath)
  if (copiedSourceFilePattern.test(relativeFilePath)) {
    violations.push({
      file: relativeFilePath,
      specifier: 'copied or backup source file',
    })
  }

  if (
    /^src[\\/]pages[\\/].*\.(?:js|jsx|ts|tsx)$/.test(relativeFilePath) &&
    !pageRouteShellPattern.test(fs.readFileSync(filePath, 'utf8').trim())
  ) {
    violations.push({
      file: relativeFilePath,
      specifier: 'page route files must be thin shells that re-export from @/components/**',
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
    componentApiForwardingShellPattern.test(fs.readFileSync(filePath, 'utf8').trim())
  ) {
    violations.push({
      file: relativeFilePath,
      specifier: 'component api forwarding shell (import the semantic @/api entry directly)',
    })
  }
}
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
const removedCompatibilityComponentEntries = new Map([
  ['AfterLease/AdjustEntries.js', 'AfterLease/Adjust*Entries.js'],
  ['AfterLease/CheckPlanListEntries.js', 'AfterLease/CheckPlan*Entries.js'],
  ['AfterLease/PolicyManageEntries.js', 'AfterLease/PolicyManage*Entries.js'],
  ['AfterLease/PolicyManageBaseEntries.js', 'AfterLease/PolicyManageBase*Entries.js'],
  ['AfterLease/CheckPlanStrategyEntries.js', 'AfterLease/CheckPlanStrategy*Entries.js'],
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
  ['Budget/PricingBusinessEntries.js', 'Budget/PricingBusiness*Entries.js'],
  ['Budget/PricingFtpInterestEntries.js', 'Budget/PricingFtpInterest*Entries.js'],
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
  ['Financial/FundDetailSectionEntries.js', 'Financial/FundDetail*Entries.js'],
  ['Financial/FundListComponentEntries.js', 'Financial/Fund*Entries.js'],
  ['Financial/FundListEntries.js', 'Financial/Fund*Entries.js'],
  ['Financial/LiquidityEntries.js', 'Financial/Liquidity*Entries.js'],
  ['Financial/PaymentDetailEntries.js', 'Financial/PaymentDetailPageEntries.js'],
  ['Financial/PaymentListEntries.js', 'Financial/Payment*Entries.js'],
  ['Report/FinancialReportListEntries.js', 'Report/FinancialReport*Entries.js'],
  ['Kpi/BaseSetEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/BaseSetModalDetailEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/BaseSetOtherConfigEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/BaseSetParameterDetailEntries.js', 'Kpi/BaseSet*Entries.js'],
  ['Kpi/KpiEstimationEntries.js', 'Kpi/Estimation*Entries.js'],
  ['Kpi/ProjectAllotListEntries.js', 'Kpi/ProjectAllot*Entries.js'],
  ['LifeCycle/LifeCycleEntries.js', 'LifeCycle/*Entries.js'],
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

const localSupportFileIncomingImports = new Map(sourceFiles.map((filePath) => [filePath, new Set()]))
for (const filePath of sourceFiles) {
  const source = fs.readFileSync(filePath, 'utf8')
  const relativeFilePath = path.relative(root, filePath)
  for (const { specifier } of extractSpecifiers(source, relativeFilePath)) {
    const resolvedImport = resolveSourceImport(filePath, specifier)
    if (resolvedImport && localSupportFileIncomingImports.has(resolvedImport)) {
      localSupportFileIncomingImports.get(resolvedImport).add(filePath)
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

if (violations.length > 0) {
  console.error('Frontend boundary violations found:')
  for (const violation of violations) {
    console.error(`- ${violation.file}: ${violation.specifier}`)
  }
  process.exit(1)
}

console.log('Frontend boundary check passed.')
