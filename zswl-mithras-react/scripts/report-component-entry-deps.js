const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const componentsDir = path.join(root, 'src', 'components')
const sourceFilePattern = /\.(js|jsx|ts|tsx)$/
const importPattern =
  /(?:import(?:[\s\S]*?from\s*)?|export(?:[\s\S]*?from\s*)?|import\s*\()\s*['"]([^'"]+)['"]/g
const componentPublicEntryPattern =
  /^@\/components\/([^/'"]+)\/([^/'"]*(?:Entries|entries)(?:\.js)?)$/

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

const edges = new Map()
const targetFanIn = new Map()

for (const filePath of walk(componentsDir)) {
  const source = fs.readFileSync(filePath, 'utf8')
  const relativeFilePath = path.relative(root, filePath)
  const [, sourceDomain] = relativeFilePath.match(/^src[\\/]components[\\/]([^\\/]+)/) || []

  if (!sourceDomain) {
    continue
  }

  let match
  while ((match = importPattern.exec(source))) {
    const specifier = match[1]
    const [, targetDomain, targetEntry] = specifier.match(componentPublicEntryPattern) || []

    if (!targetDomain || targetDomain === sourceDomain) {
      continue
    }

    const target = targetEntry ? `${targetDomain}/${targetEntry}` : targetDomain
    const edgeKey = `${sourceDomain} -> ${target}`
    const edge = edges.get(edgeKey) || {
      sourceDomain,
      target,
      files: new Set(),
      specifiers: new Set(),
    }

    edge.files.add(relativeFilePath)
    edge.specifiers.add(specifier)
    edges.set(edgeKey, edge)

    const fanIn = targetFanIn.get(target) || new Set()
    fanIn.add(sourceDomain)
    targetFanIn.set(target, fanIn)
  }
}

const sortedEdges = [...edges.values()].sort((a, b) => {
  const sourceCompare = a.sourceDomain.localeCompare(b.sourceDomain)
  if (sourceCompare !== 0) {
    return sourceCompare
  }
  return a.target.localeCompare(b.target)
})

if (sortedEdges.length === 0) {
  console.log('No cross-domain component entry dependencies found.')
  process.exit(0)
}

console.log('Component entry dependency edges:')
for (const edge of sortedEdges) {
  console.log(`- ${edge.sourceDomain} -> ${edge.target}: ${edge.files.size} file(s)`)
  for (const specifier of [...edge.specifiers].sort()) {
    console.log(`  ${specifier}`)
  }
}

console.log('\nEntry fan-in:')
for (const [target, sources] of [...targetFanIn.entries()].sort(([a], [b]) => a.localeCompare(b))) {
  console.log(`- ${target}: ${sources.size} domain(s) [${[...sources].sort().join(', ')}]`)
}
