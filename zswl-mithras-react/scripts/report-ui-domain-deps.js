const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g

const domainAliases = new Map([
  ['blackListManage', 'blackGray'],
  ['BudgetManagement', 'budget'],
  ['budgetManagement', 'budget'],
  ['CreditManage', 'credit'],
  ['creditManage', 'credit'],
  ['FilingMaterials', 'filingMaterials'],
  ['fillingMaterialsDetail', 'filingMaterials'],
  ['financialReport', 'report'],
  ['login', 'permission'],
  ['monitorEarly', 'risk'],
  ['msgNotification', 'message'],
])

const ignoredSourcePathPatterns = [
  /^src[\\/]api[\\/]/,
  /^src[\\/]pages[\\/]demo[\\/]/,
]

const orchestrationComponentRoots = new Set([
  'Process',
])

const orchestrationTargetScopes = new Set([
  'components/Process',
])

const componentAliases = new Map([
  ['CheckBusiness', 'BusinessInfoCheck'],
  ['ClientFileTable', 'ClientMaterialTable'],
  ['FileDiff', 'ChangeLogDiff'],
  ['PaymentApplyColumns', 'PaymentFtpColumns'],
])

const publicComponentRoots = new Set([
  'Actions',
  'Amount',
  'AmountRange',
  'Chart',
  'Collapse',
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
    if (publicComponentRoots.has(normalizedComponentDomain)) {
      return null
    }

    return {
      key: `components/${normalizedComponentDomain}`,
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

if (sortedEdges.length === 0) {
  console.log('No cross-domain UI dependencies found in src.')
  process.exit(0)
}

const orchestrationEdges = sortedEdges.filter(
  (edge) =>
    edge.sourceScope.startsWith('pages/') ||
    [...orchestrationComponentRoots].some((root) => edge.sourceScope === `components/${root}`) ||
    orchestrationTargetScopes.has(edge.targetScope)
)
const domainImplementationEdges = sortedEdges.filter(
  (edge) => !orchestrationEdges.includes(edge)
)

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

printEdges('Cross-domain UI dependencies from domain implementation code:', domainImplementationEdges)

console.log('')
printEdges('Cross-domain UI dependencies from page or orchestration code:', orchestrationEdges)

console.log('')
printFanIn('UI target fan-in from domain implementation code:', domainImplementationEdges)

console.log('')
printFanIn('UI target fan-in from page or orchestration code:', orchestrationEdges)
