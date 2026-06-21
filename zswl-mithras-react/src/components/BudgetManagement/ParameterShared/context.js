import { AmountEditable } from '@/components/Format'
import { amountFormat, formatPercent, hasValue, rules } from '@/utils'
import { all, create } from 'mathjs'

const mathjs = create(all)
const NUM = 10000

const runFunc = (number) => ({
  validator(r, value) {
    if (hasValue(value)) {
      const rawValue = mathjs.divide(mathjs.bignumber(value), number ?? NUM)
      if (rawValue > 100000000000) {
        return Promise.reject('不能大于100000000000')
      }
      const valStr = rawValue + ''
      const index = valStr.indexOf('.')
      if (index > 0 && valStr.substring(index + 1).length > 2) {
        return Promise.reject('小数点后不能超过2位')
      }
    }
    return Promise.resolve()
  },
})

export const myInputEditable = ({ record, dataIndex, editable, initFormat }) => {
  const { element, transform, ...rest } = AmountEditable(record, dataIndex, {
    required: true,
    disabled: false,
    inputConfig: {
      min: -9999,
      addonAfter: '%',
      formatter: undefined,
    },
    rules: [rules.required('请输入'), runFunc(1)],
    initFormat,
  })

  return editable
    ? {
        element,
        ...rest,
      }
    : false
}

export const myInputRender = (value) => {
  return hasValue(value) ? amountFormat(formatPercent(value)) : '-'
}

export const IS_FU_XIANG_BO_DONG = (value) => value === '负向波动是否反向计价'
