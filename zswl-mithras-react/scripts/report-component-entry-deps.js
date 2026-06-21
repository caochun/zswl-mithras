const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const baselinePath = path.join(__dirname, 'component-entry-deps-baseline.json')
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g
const componentPublicEntryPattern =
  /^@\/components\/([^/'"]+)\/([^/'"]*(?:Entries|entries)(?:\.js)?)$/
const {
  createDomainAliases,
  createPageSourcePathDomainAliases,
} = require('./domain-report-config')
const ignoredSourcePathPatterns = [
  /^src[\\/]pages[\\/]demo[\\/]/,
]
const publicComponentEntryRoots = new Set([
  'Chart',
])
const domainAliases = createDomainAliases({ pascalCase: true })
const args = new Set(process.argv.slice(2))

function normalizeDomain(domain) {
  return domainAliases.get(domain) || domain
}

const pageSourcePathDomainAliases = createPageSourcePathDomainAliases({ pascalCase: true })

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

function analyzeComponentEntryDeps() {
  const edges = new Map()
  const targetFanIn = new Map()

  for (const filePath of walk(srcDir)) {
    const source = fs.readFileSync(filePath, 'utf8')
    const relativeFilePath = path.relative(root, filePath)

    if (ignoredSourcePathPatterns.some((pattern) => pattern.test(relativeFilePath))) {
      continue
    }

    const sourceScope = getSourceScope(relativeFilePath)

    if (!sourceScope) {
      continue
    }

    let match
    while ((match = importPattern.exec(source))) {
      const specifier = match[1]
      const [, targetDomain, targetEntry] = specifier.match(componentPublicEntryPattern) || []

      if (publicComponentEntryRoots.has(targetDomain)) {
        continue
      }

      if (
        !targetDomain ||
        normalizeDomain(targetDomain).toLowerCase() === sourceScope.domain.toLowerCase()
      ) {
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

  const sortedEdges = [...edges.values()]
    .map((edge) => ({
      sourceScope: edge.sourceScope,
      target: edge.target,
      files: [...edge.files].sort(),
      specifiers: [...edge.specifiers].sort(),
    }))
    .sort((a, b) => {
      const sourceCompare = a.sourceScope.localeCompare(b.sourceScope)
      if (sourceCompare !== 0) {
        return sourceCompare
      }
      return a.target.localeCompare(b.target)
    })

  const sortedFanIn = [...targetFanIn.entries()]
    .map(([target, sources]) => ({
      target,
      sources: [...sources].sort(),
    }))
    .sort((a, b) => a.target.localeCompare(b.target))

  return {
    edges: sortedEdges,
    fanIn: sortedFanIn,
  }
}

function loadBaseline() {
  if (!fs.existsSync(baselinePath)) {
    return null
  }

  return JSON.parse(fs.readFileSync(baselinePath, 'utf8'))
}

function getEdgeKey(edge) {
  return `${edge.sourceScope} -> ${edge.target}`
}

function getActualComponentEntries() {
  const entries = new Set()

  for (const filePath of walk(path.join(srcDir, 'components')).filter((entryFilePath) =>
    /(?:Entries|entries)\.js$/.test(entryFilePath)
  )) {
    const entryPath = path
      .relative(path.join(srcDir, 'components'), filePath)
      .split(path.sep)
      .join('/')
    entries.add(entryPath)
    entries.add(entryPath.replace(/\.js$/, ''))
  }

  return entries
}

function findUnlistedEdges(edges, baseline) {
  const baselineEdges = new Set((baseline?.edges || []).map(getEdgeKey))
  return edges.filter((edge) => !baselineEdges.has(getEdgeKey(edge)))
}

function findStaleBaselineEdges(edges, baseline) {
  const actualEdges = new Set(edges.map(getEdgeKey))
  return (baseline?.edges || []).filter((edge) => !actualEdges.has(getEdgeKey(edge)))
}

function findDuplicateBaselineEdges(baseline) {
  const seenEdges = new Set()
  const duplicateEdges = []

  for (const edge of baseline?.edges || []) {
    const edgeKey = getEdgeKey(edge)
    if (seenEdges.has(edgeKey)) {
      duplicateEdges.push(edge)
    }
    seenEdges.add(edgeKey)
  }

  return duplicateEdges
}

function findMissingBaselineTargets(baseline) {
  const actualEntries = getActualComponentEntries()
  return (baseline?.edges || []).filter((edge) => !actualEntries.has(edge.target))
}

function printReport({ edges, fanIn }) {
  if (edges.length === 0) {
    console.log('No cross-domain component entry dependencies found in src.')
    return
  }

  console.log('Component entry dependency edges across src:')
  for (const edge of edges) {
    console.log(`- ${edge.sourceScope} -> ${edge.target}: ${edge.files.length} file(s)`)
    for (const specifier of edge.specifiers) {
      console.log(`  ${specifier}`)
    }
  }

  console.log('\nEntry fan-in:')
  for (const { target, sources } of fanIn) {
    console.log(`- ${target}: ${sources.length} scope(s) [${sources.join(', ')}]`)
  }
}

function writeBaseline(analysis) {
  fs.writeFileSync(
    baselinePath,
    `${JSON.stringify(
      {
        description:
          'Reviewed cross-domain component entry dependency edges. Additions must be intentional and documented.',
        edges: analysis.edges.map(({ sourceScope, target }) => ({
          sourceScope,
          target,
        })),
      },
      null,
      2
    )}\n`
  )
}

function main() {
  const analysis = analyzeComponentEntryDeps()

  if (args.has('--write-baseline')) {
    writeBaseline(analysis)
    console.log(`Wrote ${path.relative(root, baselinePath)} with ${analysis.edges.length} edge(s).`)
    return
  }

  if (args.has('--fail-on-unlisted')) {
    const baseline = loadBaseline()
    const unlistedEdges = findUnlistedEdges(analysis.edges, baseline)
    const staleBaselineEdges = findStaleBaselineEdges(analysis.edges, baseline)
    const duplicateBaselineEdges = findDuplicateBaselineEdges(baseline)
    const missingBaselineTargets = findMissingBaselineTargets(baseline)

    if (!baseline) {
      console.error(`Missing ${path.relative(root, baselinePath)}.`)
      process.exit(1)
    }

    if (unlistedEdges.length > 0) {
      console.error('Unlisted cross-domain component entry dependencies found:')
      for (const edge of unlistedEdges) {
        console.error(`- ${getEdgeKey(edge)}`)
      }
      process.exit(1)
    }

    if (duplicateBaselineEdges.length > 0) {
      console.error('Duplicate cross-domain component entry dependency baseline entries found:')
      for (const edge of duplicateBaselineEdges) {
        console.error(`- ${getEdgeKey(edge)}`)
      }
      process.exit(1)
    }

    if (missingBaselineTargets.length > 0) {
      console.error('Missing component entry targets in dependency baseline found:')
      for (const edge of missingBaselineTargets) {
        console.error(`- ${getEdgeKey(edge)}`)
      }
      process.exit(1)
    }

    if (staleBaselineEdges.length > 0) {
      console.error('Stale cross-domain component entry dependency baseline entries found:')
      for (const edge of staleBaselineEdges) {
        console.error(`- ${getEdgeKey(edge)}`)
      }
      process.exit(1)
    }

    console.log('No unlisted cross-domain component entry dependencies found.')
    return
  }

  printReport(analysis)
}

if (require.main === module) {
  main()
}

module.exports = {
  analyzeComponentEntryDeps,
  findDuplicateBaselineEdges,
  findMissingBaselineTargets,
  findStaleBaselineEdges,
  findUnlistedEdges,
}
