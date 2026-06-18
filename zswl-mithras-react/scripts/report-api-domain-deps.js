const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g
const apiImportPattern = /^@\/api\/([^/'"]+)(?:\/[^'"]*)?$/
const domainAliases = new Map([
  ['blackListManage', 'blackGray'],
  ['BudgetManagement', 'budget'],
  ['budgetManagement', 'budget'],
  ['FilingMaterials', 'filingMaterials'],
  ['fillingMaterialsDetail', 'filingMaterials'],
  ['financialReport', 'report'],
])

function normalizeDomain(domain) {
  return domainAliases.get(domain) || domain
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
    const [, rawTargetApiDomain] = specifier.match(apiImportPattern) || []
    const targetApiDomain = rawTargetApiDomain && normalizeDomain(rawTargetApiDomain)

    if (!targetApiDomain || targetApiDomain.toLowerCase() === sourceScope.domain.toLowerCase()) {
      continue
    }

    const edgeKey = `${sourceScope.key} -> api/${targetApiDomain}`
    const edge = edges.get(edgeKey) || {
      sourceScope: sourceScope.key,
      target: `api/${targetApiDomain}`,
      files: new Set(),
      specifiers: new Set(),
    }

    edge.files.add(relativeFilePath)
    edge.specifiers.add(specifier)
    edges.set(edgeKey, edge)

    const fanIn = targetFanIn.get(edge.target) || new Set()
    fanIn.add(sourceScope.key)
    targetFanIn.set(edge.target, fanIn)
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
  console.log('No cross-domain API dependencies found in src.')
  process.exit(0)
}

console.log('Cross-domain API dependencies across src:')
for (const edge of sortedEdges) {
  console.log(`- ${edge.sourceScope} -> ${edge.target}: ${edge.files.size} file(s)`)
  for (const specifier of [...edge.specifiers].sort()) {
    console.log(`  ${specifier}`)
  }
}

console.log('\nAPI domain fan-in:')
for (const [target, sources] of [...targetFanIn.entries()].sort(([a], [b]) => a.localeCompare(b))) {
  console.log(`- ${target}: ${sources.size} scope(s) [${[...sources].sort().join(', ')}]`)
}
