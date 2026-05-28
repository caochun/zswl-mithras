import { create, all } from 'mathjs'
import { amountStrToNumber, amountFormat } from '@/utils'

const transformValToNumber = (val) => {
  if (['', null, undefined].includes(val) || parseFloat(val).toString() == 'NaN') return 0
  return amountStrToNumber(val) || 0
}

const math = create(all, {
  // epsilon: 1e-12,
  // matrix: 'Matrix',
  // number: 'BigNumber',
  // precision: 64,
  // predictable: false,
  // randomSeed: null,
})
export default {
  bignumber: math.bignumber,
  // 加
  add(num1, num2) {
    return math.add(
      math.bignumber(transformValToNumber(num1 ?? 0)),
      math.bignumber(transformValToNumber(num2 ?? 0))
    )
  },
  // 乘
  multiply(num1, num2) {
    return math.multiply(
      math.bignumber(transformValToNumber(num1 ?? 0)),
      math.bignumber(transformValToNumber(num2 ?? 0))
    )
  },
  // 减
  subtract(num1, num2) {
    return math.subtract(
      math.bignumber(transformValToNumber(num1 ?? 0)),
      math.bignumber(transformValToNumber(num2 ?? 0))
    )
  },
  // 除
  divide(num1, num2) {
    return math.divide(
      math.bignumber(transformValToNumber(num1 ?? 0)),
      math.bignumber(transformValToNumber(num2 ?? 0))
    )
  },
  // 链式调用开始
  chain(num) {
    // console.log('num', num, transformValToNumber(0))
    // console.log(math.bignumber(transformValToNumber(0)))
    return math.chain(math.bignumber(transformValToNumber(num ?? 0)))
  },
  // 链式调用结束
  done() {
    return math.done()
  },
  // 字符串化，可以直接拿到值
  format(val) {
    return math.format(val)
  },
  // 科学计数转普通数值,带前分位
  toNonExponential(num) {
    num = Number(num)
    const m = num.toExponential().match(/\d(?:\.(\d*))?e([+-]\d+)/)
    return amountFormat(num.toFixed(Math.max(0, (m[1] || '').length - m[2])))
  },
  // 转数值
  toNonExponentialPlus(num) {
    num = Number(num)
    const m = num.toExponential().match(/\d(?:\.(\d*))?e([+-]\d+)/)
    return num.toFixed(Math.max(0, (m[1] || '').length - m[2]))
  },
  // 大于
  greaterThan(num1, num2) {
    return math.larger(
      math.bignumber(transformValToNumber(num1)),
      math.bignumber(transformValToNumber(num2))
    )
  },
}
