export function convertToChinese(money) {
  // 汉字的数字
  const cnNums = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
  // 基本单位
  const cnIntRadice = ['', '拾', '佰', '仟']
  // 对应整数部分扩展单位
  const cnIntUnits = ['', '万', '亿', '兆']
  // 对应小数部分单位
  const cnDecUnits = ['角', '分', '毫', '厘']
  // 整数金额时后面跟的字符
  const cnInteger = '整'
  // 整型完以后的单位
  const cnIntLast = '元'

  let chineseStr = '' // 输出的中文金额字符串
  let parts // 分离金额后用的数组，预定义
  if (!/^(0|\-*[1-9]\d*)(\.\d+)?$/.test(money)) return '数据非法'
  if (money == '') {
    return ''
  }
  const isLessThanZero = money.indexOf('-') > -1
  if (isLessThanZero) {
    money = money.substring(1, money.length)
  }

  money = parseFloat(money)
  if (isNaN(money) || money >= 1000000000000) {
    return ''
  }
  money = money.toFixed(2) // 转换为字符串，并保留两位小数
  parts = money.split('.')
  let integerNum = parts[0]
  let decimalNum = parts[1]
  if (parseInt(integerNum) > 0) {
    let zeroCount = 0
    let IntLen = integerNum.length
    for (let i = 0; i < IntLen; i++) {
      let n = integerNum.substr(i, 1)
      let p = IntLen - i - 1
      let q = p / 4
      let m = p % 4
      if (n == '0') {
        zeroCount++
      } else {
        if (zeroCount > 0) {
          chineseStr += cnNums[0]
        }
        zeroCount = 0
        chineseStr += cnNums[parseInt(n)] + cnIntRadice[m]
      }
      if (m == 0 && zeroCount < 4) {
        chineseStr += cnIntUnits[q]
      }
    }
    chineseStr += cnIntLast
  }
  if (decimalNum != '') {
    let decLen = decimalNum.length
    for (let i = 0; i < decLen; i++) {
      let n = decimalNum.substr(i, 1)
      if (n != '0') {
        chineseStr += cnNums[Number(n)] + cnDecUnits[i]
      }
    }
  }
  if (chineseStr == '') {
    chineseStr += cnNums[0] + cnIntLast + cnInteger
  } else if (decimalNum == '') {
    chineseStr += cnInteger
  }
  return isLessThanZero ? `负${chineseStr}` : chineseStr
}

/**
 * 转大写中文
 */
// export function convertToChinese(n) {
//   /*  if (!/^(0|[1-9]\d*)(\.\d+)?$/.test(n))
//        return "数据非法"; */
//   if (!/^(0|\-*[1-9]\d*)(\.\d+)?$/.test(n)) return '数据非法'
//   var fuhao = n.toString().indexOf('-') == 0 ? '负' : ''

//   var unit = '千百拾亿千百拾万千百拾元角分',
//     str = ''
//   n += '00'
//   //如果是负数就就截取
//   if (fuhao == '负') {
//     n = n.substring(1, n.length)
//   }
//   var p = n.indexOf('.')
//   if (p >= 0) n = n.substring(0, p) + n.substr(p + 1, 2)
//   unit = unit.substr(unit.length - n.length)
//   for (var i = 0; i < n.length; i++)
//     str += '零壹贰叁肆伍陆柒捌玖'.charAt(n.charAt(i)) + unit.charAt(i)
//   return (
//     fuhao +
//     str
//       .replace(/零(千|百|拾|角)/g, '零')
//       .replace(/(零)+/g, '零')
//       .replace(/零(万|亿|元)/g, '$1')
//       .replace(/(亿)万|壹(拾)/g, '$1$2')
//       .replace(/^元零?|零分/g, '')
//       .replace(/元$/g, '元整')
//   )
// }
