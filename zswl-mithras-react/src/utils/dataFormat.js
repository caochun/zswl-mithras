export const compareDetail = (data = {}) => {
  const newDetail = {}
  const detail = {}
  const isLog = {}
  Object.entries(data).forEach(([field, res]) => {
    const { value, beforeValue, isChange } = res ?? {}
    const notObject = [undefined, null].includes(res) || typeof res !== 'object'
    newDetail[field] = notObject ? res : value
    detail[field] = beforeValue
    if (isChange) isLog[field] = true
  })
  return { newDetail, detail, isLog }
}

export const compareTableData = (res = []) => {
  const data = res.map((v) => compareDetail(v))
  const newDetail = data.map((v) => v.newDetail)
  const detail = data.map((v) => v.detail)
  const isLog = data.map((v) => v.isLog)
  return { newDetail, detail, isLog }
}
