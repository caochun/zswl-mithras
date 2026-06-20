import { amountStrToNumber, hasValue } from '@/utils'

export const validatorAmount = ({ getFieldValue }) => ({
  validator(_, value) {
    const applyCreditAmountNumber = amountStrToNumber(getFieldValue('applyCreditAmount'))
    const valueNumber = amountStrToNumber(value)
    if (valueNumber < 0) {
      return Promise.reject(new Error('金额不能小于0！'))
    }
    if (!hasValue(value)) {
      return Promise.resolve()
    }
    if (valueNumber > applyCreditAmountNumber) {
      return Promise.reject(new Error('金额需少于申报授信金额！'))
    }
    return Promise.resolve()
  },
})

export const validatorBigZero = ({}) => ({
  validator(_, value) {
    const valueNumber = amountStrToNumber(value)
    if (valueNumber < 0) {
      return Promise.reject(new Error('输入值不能小于0！'))
    }
    return Promise.resolve()
  },
})

export const validatorNoZero = ({}) => ({
  validator(_, value) {
    if (value === 0 || value === '0') {
      return Promise.reject(new Error('输入值不能为0！'))
    }
  },
})

export const creditAmountLoopOptions = [
  {
    label: '否',
    value: 0,
  },
  {
    label: '是',
    value: 1,
  },
]
