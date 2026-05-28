/* eslint-disable */

import { cloneElement, isValidElement } from 'react'
import { create, all } from 'mathjs'
import { amountFormat, hasValue } from '@/utils'

const mathjs = create(all)
/*
1.金额在展示的时候 -----除于10000
2.金额在传值的时候，-------乘于10000
3.场景  表单的新增和表单编辑 ， table列表的展示。
 */
const NUM = 10000
function Amount({ value, children, onChange }) {
  const handleChange = (val) => {
    onChange(mathjs.multiply(mathjs.bignumber(val), NUM).toString())
  }
  const displayValue = mathjs.divide(mathjs.bignumber(value || 0), NUM) + ''
  if (isValidElement(children)) {
    return cloneElement(children, {
      type: 'number',
      stringMode: true,
      value: value == 0 ? 0 : displayValue === '0' ? undefined : displayValue,
      onChange: handleChange,
    })
  }
  return amountFormat(displayValue) || null
}
const runFunc = (number, min = 0, max = 100000000000, precision = 2) => ({
  validator(r, value) {
    if (hasValue(value)) {
      const rawValue = mathjs.divide(mathjs.bignumber(value), number ?? NUM)
      if (rawValue < min) {
        return Promise.reject(`不能小于${min}`)
      }
      if (rawValue > max) {
        return Promise.reject(`不能大于${max}`)
      }
      const valStr = rawValue + ''
      const index = valStr.indexOf('.')
      if (index > 0 && valStr.substring(index + 1).length > precision) {
        return Promise.reject(`小数点后不能超过${precision}位`)
      }
    }
    return Promise.resolve()
  },
})

const serviceFunc = (cb) => ({
  async validator(r, value){
    const res = await cb(value)
    if(!res){
      return Promise.reject('授信额度不足')
    }
    return Promise.resolve()
  }
})

Amount.rule = runFunc()
Amount.ruleFunc = runFunc
Amount.serviceFunc = serviceFunc

export default Amount
