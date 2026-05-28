import { amountFormat, formatPercent, hasValue } from '@/utils'
import { Form } from '@zswl/components'
import { InputNumber, Tooltip, Typography } from 'antd'
import _, { isNumber } from 'lodash'
import { useMemo } from 'react'
import math from '@/utils/math'
import numeral from 'numeral'

// 100亿
const MAX_VALUE = 100000000000
const INIT_NUMBER = 10000
function FormAmount({ value, onChange, onBlur, initFormat = INIT_NUMBER, precision = 2, ...rest }) {
  const newValue = useMemo(() => {
    if (!hasValue(value)) return undefined
    const bigNumber = math.toNonExponentialPlus(math.format(math.divide(value, initFormat)))
    return bigNumber
  }, [value, initFormat])

  const numberChange = (val) => {
    const newNumber = hasValue(val)
      ? math.toNonExponentialPlus(math.format(math.multiply(val, initFormat)))
      : val
    onChange?.(newNumber)
  }

  const numberBlur = (e) => {
    const val = e.target.value
    const newValue = numeral(val).value()
    const newNumber = hasValue(val)
      ? math.toNonExponentialPlus(math.format(math.multiply(newValue, initFormat)))
      : val
    onBlur?.(newNumber)
  }
  return (
    <InputNumber
      stringMode
      value={newValue}
      onChange={numberChange}
      formatter={(value) => {
        return (
          value &&
          numeral(value).format(`0,0.[${Array.from({ length: precision }, () => 0).join('')}]`)
        )
        // 导致小数点后千分位
        // return `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
      }}
      min={0}
      max={MAX_VALUE}
      parser={(number) => {
        return number.replace(/\$\s?|(,*)/g, '')
      }}
      placeholder="请输入"
      style={{ width: '100%' }}
      onBlur={numberBlur}
      precision={precision}
      {...rest}
    />
  )
}

const runFunc = ({ initFormat, max = MAX_VALUE, min = 0, isRequired }) => ({
  required: isRequired,
  validator(r, value) {
    if (isRequired && !hasValue(value)) {
      return Promise.reject('请输入')
    }
    if (hasValue(value)) {
      const rawValue = math.divide(math.bignumber(value), initFormat ?? INIT_NUMBER)
      if (rawValue < min) {
        return Promise.reject(`不能小于${min}`)
      }
      if (rawValue > max) {
        return Promise.reject(`不能大于${amountFormat(max)}`)
      }
      const valStr = rawValue + ''
      const index = valStr.indexOf('.')
      if (index > 0 && valStr.substring(index + 1).length > 4) {
        return Promise.reject('小数点后不能超过2位')
      }
    }
    return Promise.resolve()
  },
})

const Item = ({
  isRequired = true,
  rules,
  name,
  label,
  style,
  max,
  min,
  labelCol,
  initFormat = INIT_NUMBER,
  noStyle = false,
  ...props
}) => {
  const newRules = useMemo(() => {
    return _.isArray(rules)
      ? [...rules, runFunc({ initFormat, max, min, isRequired })]
      : [runFunc({ initFormat, max, min, isRequired })]
  }, [rules, isRequired])
  const transform = (value) => {
    return {
      [name]: hasValue(value)
        ? math.multiply(math.bignumber(value), initFormat).toString()
        : undefined,
    }
  }
  return (
    <Form.Item
      name={name}
      rules={newRules}
      label={label}
      style={style}
      labelCol={labelCol}
      noStyle={noStyle}
    >
      <FormAmount max={max} min={min} initFormat={initFormat} {...props} />
    </Form.Item>
  )
}

FormAmount.Item = Item
FormAmount.runFunc = runFunc
FormAmount.parser = (value) => value.replace(/\$\s?|(,*)/g, '')
FormAmount.getNumber = ({ value, initFormat = INIT_NUMBER, precision = 2, needSmallNumber = true }) => {
  const newValue = value?.value !== undefined ? value?.value : value
  const title = amountFormat(formatPercent(newValue, initFormat), precision, needSmallNumber)
  return title
}
FormAmount.Format = ({
  value,
  initFormat,
  precision = 2,
  suffix = '',
  style = {},
  needSmallNumber,
}) => {
  const isChange = value?.isChange
  const title = FormAmount.getNumber({ value, initFormat, precision, needSmallNumber })

  return (
    <Tooltip title={title}>
      <span style={{ color: isChange && 'red', ...style }}>
        {[null, undefined].includes(title) ? '-' : `${title}${suffix}`}
      </span>
    </Tooltip>
  )
}
FormAmount.getNumber = ({ value, initFormat = INIT_NUMBER, precision = 2, needSmallNumber }) => {
  const newValue = value?.value !== undefined ? value?.value : value
  const title = amountFormat(formatPercent(newValue, initFormat), precision, needSmallNumber)
  return title
}
export default FormAmount
