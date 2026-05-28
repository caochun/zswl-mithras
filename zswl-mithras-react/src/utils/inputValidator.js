import numeral from 'numeral'
import { amountStrToNumber, hasValue } from '@/utils'

// 方法返回Form.Item 组件getValueFromEvent，设置如何将 event 的值转换成字段值
export function getInputNumberValueFromEvent(event, decimal = 2) {
  // //先替换掉千位分隔符，如果此处未替换，下面的值会混乱
  const value = event.target.value.replace(/,/g, '')
  if (!value) {
    return value
  }

  if (/\-?[0-9]+.?[0-9]*$/.test(value) || value === '-') {
    if (value < -999.99) {
      return ''
    }

    // 限制4位小数
    if (decimal === 4) {
      const numHasDecimal4 = value.toString().replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')
      const numHasDecimal = numHasDecimal4.split('.')
      numHasDecimal[0] = numHasDecimal[0].replace(/(?=(\B\d{3})+$)/g, ',')
      return numHasDecimal.join('.')
    }
    // 默认限制2位小数，添加千位千位分隔符
    return value
      .toString()
      .replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')
      .replace(/(?=(\B\d{3})+$)/g, ',')
  }
  return ''
}

// 方法返回InputNumber 组件接收的属性值,月份输入
export function getInputNumberMonthProps(val) {
  return {
    min: 0,
    max: 9999,
    precision: 0,
    decimalSeparator: 0,
    formatter: (value) => {
      return value.replace(/(?=(\B\d{3})+$)/g, ',')
    },
    ...val,
  }
}

// 方法返回InputNumber 组件接收的属性值,月份输入
export function getInputNumberProps(val) {
  return {
    min: 0,
    max: 100000000000,
    precision: 0,
    decimalSeparator: 0,
    ...val,
  }
}

// 方法返回InputNumber 组件接收的属性值，普通数字输入
export function getInputNumberAmountProps(val) {
  return {
    min: 0,
    max: 100000000000,
    precision: 2,
    formatter: (value) => {
      return value && numeral(value).format('0,0.[0000]')
    },
    ...val,
  }
}

const NumberToFixed = (num, decimals) =>
  num.toLocaleString('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: decimals,
  })
export function amountFormat(num, precision = 2, needSmallNumber = true) {
  if (!num) {
    return num
  }
  if (needSmallNumber && num > 0 && num < 0.01) {
    return '<0.01'
  }
  num = parseFloat(num) // 转换成浮点数
  if (Number.isNaN(num)) {
    return '-'
  }
  num = NumberToFixed(num, precision) // 保留指定小数位数，并四舍五入

  return num
}

// 输入值不能小于0，或者大于100000000000
export function validatorRange() {
  return {
    validator(r, value) {
      if (value < 0) {
        return Promise.reject(new Error('输入值不能小于0'))
      }
      if (value > 100000000000) {
        return Promise.reject('不能大于100,000,000,000')
      }
      return Promise.resolve()
    },
  }
}

// 输入值不能大于100,000,000,000
export function validatorMax() {
  return {
    validator(r, value) {
      if (value > 100000000000) {
        return Promise.reject('不能大于100,000,000,000')
      }
      return Promise.resolve()
    },
  }
}
// 输入值不能小于0
export const validatorBigZero = () => ({
  validator(_, value) {
    const valueNumber = amountStrToNumber(value)
    if (valueNumber < 0) {
      return Promise.reject(new Error('输入值不能小于0'))
    }
    return Promise.resolve()
  },
})

// 输入值不能为0
export const validatorNoZero = () => ({
  validator(_, value) {
    if (value === 0 || value === '0') {
      return Promise.reject(new Error('输入值不能为0'))
    }
    return Promise.resolve()
  },
})

// 输入值大于等于0
export const validatorBigThenZero = () => ({
  validator(_, value) {
    if (hasValue(value)) {
      const valueNumber = amountStrToNumber(value)
      if (valueNumber < 0) {
        return Promise.reject(new Error('输入值须为正数'))
      }
    }
    return Promise.resolve()
  },
})

// email
export const validatorEmail = () => ({
  validator(_, value) {
    const reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/
    if (!reg.test(value)) {
      return Promise.reject(new Error('请输入正确的邮箱格式！'))
    }
    return Promise.resolve()
  },
})

// 判断是否统一社会信用代码 宽松版
export const isUnifiedCreditCode = (code) => {
  return /^(([0-9A-Za-z]{15})|([0-9A-Za-z]{18})|([0-9A-Za-z]{20}))$/.test(code)
}
