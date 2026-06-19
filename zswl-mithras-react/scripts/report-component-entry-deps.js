const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g
const componentPublicEntryPattern =
  /^@\/components\/([^/'"]+)\/([^/'"]*(?:Entries|entries)(?:\.js)?)$/
const domainAliases = new Map([
  ['blackListManage', 'BlackGray'],
  ['fillingMaterialsDetail', 'FilingMaterials'],
  ['financialReport', 'Report'],
  ['monitorEarly', 'Risk'],
  ['msgNotification', 'Message'],
  ['overdueListSearch', 'Risk'],
  ['ProfitDistribution', 'Budget'],
])

function normalizeDomain(domain) {
  return domainAliases.get(domain) || domain
}

const pageSourcePathDomainAliases = [
  {
    pattern: /^src[\\/]pages[\\/]afterLease[\\/]checkPlan[\\/]singleViewRisk(?:[\\/]|$)/,
    key: 'pages/afterLease/checkPlan/singleViewRisk',
    domain: 'Customer',
  },
  {
    pattern: /^src[\\/]pages[\\/]customerView(?:[\\/]|$)/,
    key: 'pages/customerView',
    domain: 'Customer',
  },
  {
    pattern: /^src[\\/]pages[\\/]lease[\\/]tracking(?:[\\/]|$)/,
    key: 'pages/lease/tracking',
    domain: 'TrackEvent',
  },
]

const sourceAreaScopes = new Map([
  ['afterLease', { key: 'components/AfterLease', domain: 'AfterLease' }],
  ['blackGray', { key: 'components/BlackGray', domain: 'BlackGray' }],
  ['budgetManagement', { key: 'components/BudgetManagement', domain: 'BudgetManagement' }],
  ['cpm', { key: 'components/Cpm', domain: 'Cpm' }],
  ['customer', { key: 'components/Customer', domain: 'Customer' }],
  ['dashboard', { key: 'components/Dashboard', domain: 'Dashboard' }],
  ['kpi', { key: 'components/Kpi', domain: 'Kpi' }],
  ['process', { key: 'components/Process', domain: 'Process' }],
  ['report', { key: 'components/Report', domain: 'Report' }],
  ['risk', { key: 'components/Risk', domain: 'Risk' }],
  ['rzy', { key: 'pages/rzy', domain: 'rzy' }],
])

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
  const [, utilityDomain] =
    relativeFilePath.match(/^src[\\/]utils[\\/]domains[\\/]([^\\/]+)/) || []
  if (utilityDomain) {
    return {
      key: `utils/domains/${utilityDomain}`,
      domain: normalizeDomain(utilityDomain),
    }
  }

  const pagePathAlias = pageSourcePathDomainAliases.find(({ pattern }) =>
    pattern.test(relativeFilePath)
  )
  if (pagePathAlias) {
    return {
      key: pagePathAlias.key,
      domain: normalizeDomain(pagePathAlias.domain),
    }
  }

  const [, componentDomain] = relativeFilePath.match(/^src[\\/]components[\\/]([^\\/]+)/) || []
  if (componentDomain) {
    return {
      key: `components/${componentDomain}`,
      domain: normalizeDomain(componentDomain),
    }
  }

  const [, pageDomain] = relativeFilePath.match(/^src[\\/]pages[\\/]([^\\/]+)/) || []
  if (pageDomain) {
    return {
      key: `pages/${pageDomain}`,
      domain: normalizeDomain(pageDomain),
    }
  }

  const [, sourceArea] = relativeFilePath.match(/^src[\\/]([^\\/]+)/) || []
  if (sourceArea) {
    const aliasedScope = sourceAreaScopes.get(sourceArea)
    if (aliasedScope) {
      return aliasedScope
    }

    return {
      key: sourceArea,
      domain: normalizeDomain(sourceArea),
    }
  }

  return null
}

const edges = new Map()
const targetFanIn = new Map()

for (const filePath of walk(srcDir)) {
  const source = fs.readFileSync(filePath, 'utf8')
  const relativeFilePath = path.relative(root, filePath)
  const sourceScope = getSourceScope(relativeFilePath)

  if (!sourceScope) {
    continue
  }

  let match
  while ((match = importPattern.exec(source))) {
    const specifier = match[1]
    const [, targetDomain, targetEntry] = specifier.match(componentPublicEntryPattern) || []

    if (!targetDomain || normalizeDomain(targetDomain).toLowerCase() === sourceScope.domain.toLowerCase()) {
      continue
    }

    const target = targetEntry ? `${targetDomain}/${targetEntry}` : targetDomain
    const edgeKey = `${sourceScope.key} -> ${target}`
    const edge = edges.get(edgeKey) || {
      sourceScope: sourceScope.key,
      target,
      files: new Set(),
      specifiers: new Set(),
    }

    edge.files.add(relativeFilePath)
    edge.specifiers.add(specifier)
    edges.set(edgeKey, edge)

    const fanIn = targetFanIn.get(target) || new Set()
    fanIn.add(sourceScope.key)
    targetFanIn.set(target, fanIn)
  }
}

const sortedEdges = [...edges.values()].sort((a, b) => {
  const sourceCompare = a.sourceScope.localeCompare(b.sourceScope)
  if (sourceCompare !== 0) {
    return sourceCompare
  }
  return a.target.localeCompare(b.target)
})

if (sortedEdges.length === 0) {
  console.log('No cross-domain component entry dependencies found in src.')
  process.exit(0)
}

console.log('Component entry dependency edges across src:')
for (const edge of sortedEdges) {
  console.log(`- ${edge.sourceScope} -> ${edge.target}: ${edge.files.size} file(s)`)
  for (const specifier of [...edge.specifiers].sort()) {
    console.log(`  ${specifier}`)
  }
}

console.log('\nEntry fan-in:')
for (const [target, sources] of [...targetFanIn.entries()].sort(([a], [b]) => a.localeCompare(b))) {
  console.log(`- ${target}: ${sources.size} scope(s) [${[...sources].sort().join(', ')}]`)
}
