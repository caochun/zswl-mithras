export const getFinancialUrl = (code, id) => {
  const isDirect = code.startsWith('ZR')
  return isDirect ? `/financial/direct/detail/${id}` : `/financial/fund/detail/${id}`
}
