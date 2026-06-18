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
const legacyApiPrefixes = new Map([
  ['@/api/financial/accountsReceivable', '@/api/budget/accountsReceivable'],
])
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

function getLegacyApiPrefixReplacement(specifier) {
  for (const [legacyPrefix, replacementPrefix] of legacyApiPrefixes) {
    if (specifier === legacyPrefix || specifier.startsWith(`${legacyPrefix}/`)) {
      return replacementPrefix
    }
  }

  return null
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
    const legacyApiPrefixReplacement = getLegacyApiPrefixReplacement(specifier)
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
    } else if (legacyApiPrefixReplacement) {
      violations.push({
        file: relativeFilePath,
        specifier: `${specifier} (use ${legacyApiPrefixReplacement} semantic entry)`,
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
