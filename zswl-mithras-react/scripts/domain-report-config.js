const domainAliasPairs = [
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
  ['overdueListSearch', 'risk'],
  ['ProfitDistribution', 'budget'],
]

const extraApiDomainAliasPairs = [
  ['cpm', 'cpm'],
  ['customer', 'customer'],
  ['dashboard', 'dashboard'],
  ['utils', 'common'],
]

const pageSourcePathDomainAliasDefs = [
  {
    pattern: /^src[\\/]pages[\\/]afterLease[\\/]checkPlan[\\/]singleViewRisk(?:[\\/]|$)/,
    key: 'pages/afterLease/checkPlan/singleViewRisk',
    domain: 'customer',
  },
  {
    pattern: /^src[\\/]pages[\\/]budgetManagement[\\/]provisionForecast(?:[\\/]|$)/,
    key: 'pages/budgetManagement/provisionForecast',
    domain: 'budget',
  },
  {
    pattern: /^src[\\/]pages[\\/]customerView(?:[\\/]|$)/,
    key: 'pages/customerView',
    domain: 'customer',
  },
  {
    pattern: /^src[\\/]pages[\\/]lease[\\/]tracking(?:[\\/]|$)/,
    key: 'pages/lease/tracking',
    domain: 'trackEvent',
  },
]

function upperFirst(value) {
  return value ? `${value[0].toUpperCase()}${value.slice(1)}` : value
}

function createDomainAliases({ pascalCase = false, includeApiExtras = false } = {}) {
  const pairs = includeApiExtras
    ? [...domainAliasPairs, ...extraApiDomainAliasPairs]
    : domainAliasPairs
  return new Map(pairs.map(([key, value]) => [key, pascalCase ? upperFirst(value) : value]))
}

function createPageSourcePathDomainAliases({ pascalCase = false } = {}) {
  return pageSourcePathDomainAliasDefs.map((item) => ({
    ...item,
    domain: pascalCase ? upperFirst(item.domain) : item.domain,
  }))
}

module.exports = {
  createDomainAliases,
  createPageSourcePathDomainAliases,
}
