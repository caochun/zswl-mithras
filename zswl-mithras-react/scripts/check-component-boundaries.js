const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
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
const pageImportPattern = /^@\/pages\//

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

const violations = []

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

    const [, targetComponentEntryDomain] = specifier.match(componentEntryPathPattern) || []
    if (isPageImport) {
      violations.push({
        file: relativeFilePath,
        specifier,
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

if (violations.length > 0) {
  console.error('Frontend boundary violations found:')
  for (const violation of violations) {
    console.error(`- ${violation.file}: ${violation.specifier}`)
  }
  process.exit(1)
}

console.log('Frontend boundary check passed.')
