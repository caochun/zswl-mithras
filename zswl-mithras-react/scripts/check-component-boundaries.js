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
  ['EvaluationAgency', 'EvaluationAgency/EvaluationAgencyEntries'],
  ['FileDiff', 'FileDiff/FileDiffEntries'],
  ['JumpClient', 'JumpClient/JumpClientEntries'],
  ['PaymentApplyColumns', 'PaymentApplyColumns/PaymentApplyColumnsEntries'],
  ['Policy', 'Policy/PolicyEntries'],
  ['PolicyColumns', 'PolicyColumns/PolicyColumnsEntries'],
  ['UpdateRatingInfoButton', 'UpdateRatingInfoButton/UpdateRatingInfoButtonEntries'],
  ['ZhongDengButton', 'ZhongDengButton/ZhongDengButtonEntries'],
])
const componentRootImportPattern = /^@\/components\/([^/'"]+)$/
const pageImportPattern = /^@\/pages\//
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
    legacyPrefix: '@/api/financial/fundApi',
    replacementPrefix: '@/api/contract/lprApi or @/api/financial/fundApi in Financial domain',
    allowedSourceDomains: ['Financial'],
    allowedSourcePathPrefixes: [
      'src/api/contract/lprApi.js',
      'src/pages/financial/',
    ],
  },
  {
    legacyPrefix: '@/api/approval/processModifyRemarkApi',
    replacementPrefix:
      '@/api/<domain>/approvalRemarkApi or shared approval/detail components',
    allowedSourceDomains: ['Actions', 'Table'],
    allowedSourcePathPrefixes: [
      'src/api/contract/approvalRemarkApi.js',
      'src/api/credit/approvalRemarkApi.js',
      'src/api/financial/approvalRemarkApi.js',
      'src/api/project/approvalRemarkApi.js',
      'src/pages/process/',
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
    replacementPrefix: '@/api/lifeCycle/riskWarningApi or @/api/risk/publicMonitor in Risk domain',
    allowedSourceDomains: ['Risk'],
    allowedSourcePathPrefixes: [
      'src/api/lifeCycle/riskWarningApi.js',
      'src/pages/risk/',
      'src/pages/process/',
    ],
  },
  {
    legacyPrefix: '@/api/customer/customerRat/customerRatApi',
    replacementPrefix: '@/api/project/ratingApi or @/api/customer/customerRat/customerRatApi in Customer/process utilities',
    allowedSourceDomains: ['Customer'],
    allowedSourcePathPrefixes: [
      'src/api/project/ratingApi.js',
      'src/customer/CustomerRatUtils.js',
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
  if (!componentEntryImports.has(entryPath)) {
    violations.push({
      file: `src/components/${entryPath}`,
      specifier: 'unused component entry',
    })
  }

  if (!documentedComponentEntrySet.has(entryPath)) {
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
