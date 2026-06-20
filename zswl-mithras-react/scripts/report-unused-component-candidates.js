const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const srcDir = path.join(root, 'src')
const componentDir = path.join(srcDir, 'components')
const args = new Set(process.argv.slice(2))

const sourceExtensions = ['.js', '.jsx', '.ts', '.tsx']
const styleExtensions = ['.less']
const scannableExtensions = [...sourceExtensions, ...styleExtensions]
const candidateExtensions = scannableExtensions

const includeIndex = args.has('--include-index')
const includeEntries = args.has('--include-entries')
const includeDeclarations = args.has('--include-declarations')
const jsonOutput = args.has('--json')

const importPattern =
  /(?:import\s+['"]([^'"]+)['"]|import\s+[^'"]*?\s+from\s+['"]([^'"]+)['"]|export\s+[^'"]*?\s+from\s+['"]([^'"]+)['"]|import\s*\(\s*['"]([^'"]+)['"]|require\s*\(\s*['"]([^'"]+)['"])/g
const styleImportPattern = /@import\s+(?:\([^)]*\)\s*)?['"]~?([^'"]+)['"]/g

function walk(dir, files = []) {
  if (!fs.existsSync(dir)) {
    return files
  }
  for (const item of fs.readdirSync(dir, { withFileTypes: true })) {
    const fullPath = path.join(dir, item.name)
    if (item.isDirectory()) {
      if (!['node_modules', 'dist', 'build'].includes(item.name)) {
        walk(fullPath, files)
      }
      continue
    }
    files.push(fullPath)
  }
  return files
}

function toRelative(file) {
  return path.relative(root, file).replace(/\\/g, '/')
}

function hasKnownExtension(file) {
  return scannableExtensions.includes(path.extname(file))
}

function resolveImport(fromFile, specifier) {
  if (!specifier.startsWith('.') && !specifier.startsWith('@/')) {
    return null
  }

  const base = specifier.startsWith('@/')
    ? path.join(srcDir, specifier.slice(2))
    : path.resolve(path.dirname(fromFile), specifier)

  const candidates = []
  if (hasKnownExtension(base)) {
    candidates.push(base)
  } else {
    for (const extension of scannableExtensions) {
      candidates.push(`${base}${extension}`)
    }
    for (const extension of scannableExtensions) {
      candidates.push(path.join(base, `index${extension}`))
    }
  }

  return candidates.find((candidate) => fs.existsSync(candidate)) || null
}

function collectImports(file) {
  const text = fs.readFileSync(file, 'utf8')
  const imports = []
  const patterns = [importPattern]
  if (path.extname(file) === '.less') {
    patterns.push(styleImportPattern)
  }

  for (const pattern of patterns) {
    pattern.lastIndex = 0
    let match
    while ((match = pattern.exec(text))) {
      imports.push(match.slice(1).find(Boolean))
    }
  }

  return imports
}

function isCandidate(file) {
  const relative = toRelative(file)
  const extension = path.extname(file)
  const basename = path.basename(file)

  if (!file.startsWith(componentDir + path.sep)) {
    return false
  }
  if (!candidateExtensions.includes(extension)) {
    return false
  }
  if (!includeDeclarations && relative.endsWith('.d.ts')) {
    return false
  }
  if (!includeEntries && /Entries\.(js|jsx|ts|tsx)$/.test(relative)) {
    return false
  }
  if (!includeIndex && /^index\.(js|jsx|ts|tsx|less)$/.test(basename)) {
    return false
  }

  return true
}

const scannableFiles = walk(srcDir).filter((file) =>
  scannableExtensions.includes(path.extname(file)),
)
const inbound = new Map(scannableFiles.map((file) => [file, new Set()]))

for (const file of scannableFiles) {
  for (const specifier of collectImports(file)) {
    const resolved = resolveImport(file, specifier)
    if (!resolved || !inbound.has(resolved)) {
      continue
    }
    inbound.get(resolved).add(file)
  }
}

const candidates = scannableFiles
  .filter(isCandidate)
  .filter((file) => inbound.get(file)?.size === 0)
  .map((file) => toRelative(file))
  .sort()

if (jsonOutput) {
  console.log(JSON.stringify({ count: candidates.length, candidates }, null, 2))
} else if (candidates.length) {
  console.log('Unused component candidates:')
  for (const candidate of candidates) {
    console.log(`- ${candidate}`)
  }
  console.log(`\nCount: ${candidates.length}`)
  console.log(
    'Note: this is a static candidate report. Review each file before deleting; dynamic conventions may not be visible to the scanner.',
  )
} else {
  console.log('No unused component candidates found.')
}
