export const disabledQuarterEndMonth = (current) => {
  const month = current.month() + 1
  return ![3, 6, 9, 12].includes(month)
}
