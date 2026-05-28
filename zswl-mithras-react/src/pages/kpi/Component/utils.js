import { hasValue } from '@/utils'

export const formulaData = (value) => {
  if (hasValue(value) && String(value).indexOf('=') === 0) {
    return {
      value: value.slice(1),
      configValueType: 'FORMULA',
    }
  }
  return {
    value,
    configValueType: 'VALUE',
  }
}
