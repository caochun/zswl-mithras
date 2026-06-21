import { InputNumber } from 'antd'
import { useCallback } from 'react'

// moneySymbol = '¥',
const DefaultPrecisionCont = 2

const CpmAmountNumber = (props) => {
  const {
    precision = 2,
    moneySymbol = '',
    onBlur,
    mode,
    placeholderValue = '请输入',
    type, // 无用
    ...rest
  } = props

  const getTextByLocale = (text) => {
    let moneyText = `${text}`?.replaceAll(',', '')
    if (typeof moneyText === 'string') {
      const parsedNum = Number(moneyText)
      // 转换数字为NaN时，返回原始值展示
      if (Number.isNaN(parsedNum)) return moneyText
      moneyText = parsedNum
    }
    if (!moneyText && moneyText !== 0) return ''
    return `${moneySymbol || ''}${getFormateValue(text)}`
  }

  // 数字转千分位
  const getFormateValue = useCallback(
    (value) => {
      // 新建数字正则，需要配置小数点
      const reg = new RegExp(
        `\\B(?=(\\d{${3 + Math.max(precision - DefaultPrecisionCont, 0)}})+(?!\\d))`,
        'g'
      )
      // 切分为 整数 和 小数 不同
      const [intStr, floatStr] = String(value).split('.')
      // 最终的数据string，需要去掉 , 号。
      const resultInt = intStr.replace(reg, ',')
      // 计算最终的小数点
      let resultFloat = ''
      if (floatStr && precision > 0) {
        resultFloat = `.${floatStr.slice(
          0,
          precision === undefined ? DefaultPrecisionCont : precision
        )}`
      }
      return `${resultInt}${resultFloat}`
    },
    [precision]
  )

  // 替换千分位
  const replaceThousands = (text) => {
    return text.replace(new RegExp(`\\${moneySymbol}\\s?|(,*)`, 'g'), '')
  }

  // 只读模式
  if (mode === 'read') {
    return <span>{getTextByLocale(props.value)}</span>
  }

  return (
    <InputNumber
      style={{ width: '100%' }}
      precision={precision}
      placeholder={placeholderValue}
      formatter={(value) => {
        if (value) {
          return `${getFormateValue(value)}`
        }
        return value?.toString()
      }}
      parser={(value) => {
        if (value) {
          value = replaceThousands(value)
        }
        return value
      }}
      {...rest}
      onBlur={
        onBlur
          ? (e) => {
              let value = e.target.value
              if (value) {
                value = replaceThousands(value)
              }
              onBlur?.(value)
            }
          : undefined
      }
    ></InputNumber>
  )
}

export default CpmAmountNumber
