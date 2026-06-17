import { amountFormat as amountFormatFunc, hasValue } from '@/utils'
import { App } from '@zswl/components'
import { Tooltip } from 'antd'
import _ from 'lodash'
import IconFont from '@/components/Icon'
import { useMemo } from 'react'

const nzhcn = require('nzh/cn')

export const JSONRender = (text) => {
  if (!text) return undefined
  const title = <pre>{JSON.stringify(JSON.parse(text), null, 2)}</pre>
  return (
    <Tooltip
      title={title}
      placement="topLeft"
      overlayInnerStyle={{ width: 500, overflowY: 'auto', maxHeight: 350 }}
      getPopupContainer={() => document.body}
    >
      <div className="z-single-line" style={{ maxWidth: 200 }}>
        {text}
      </div>
    </Tooltip>
  )
}

export const AmountFormat = ({
  value,
  isChange,
  unit = '',
  initFormat = 10000,
  precision = 2,
  needSmallNumber = true,
}) => {
  return (
    <div style={{ color: isChange && 'red' }}>
      {hasValue(value)
        ? amountFormatFunc(value / initFormat, precision, needSmallNumber) + unit
        : '-'}
    </div>
  )
}

export const PercentageRender = (val, initFormat = 10000) => {
  const value = val?.value ?? val

  const newValue = hasValue(value) ? amountFormatFunc(value / initFormat) : '-'
  return <FiledFormat title={newValue} isChange={val?.isChange} unit={val?.unit} />
}

export const getValue = (value) => {
  if (_.isObject(value) && !_.isArray(value)) return value.value
  return value
}
const isEmpty = (val) => {
  if (val === 0 || val === '0') return false
  if (_.isNumber(val)) return false
  return _.isEmpty(val)
}
export const FiledFormat = ({
  title,
  value,
  isChange,
  needBar = true,
  hasToolTip = true,
  unit = '',
  width,
  needWrap = false,
}) => {
  const titleValue = getValue(title) ?? getValue(value)
  const isRed = isChange ?? value?.isChange

  const hasTitle = !needBar || !isEmpty(titleValue)
  return hasTitle ? (
    <Tooltip
      title={hasToolTip ? `${titleValue}${unit}` : ''}
      placement="top"
      getPopupContainer={() => document.body}
    >
      <div style={{ width, overflow: 'hidden', textOverflow: 'ellipsis' }}>
        {`${titleValue ?? ''}`?.split('\n').map((v, i) => {
          if (needWrap) {
            return (
              <div style={{ color: isRed && 'red' }} key={i}>
                {v}
                {unit}
              </div>
            )
          }
          return (
            <span style={{ color: isRed && 'red' }} key={i}>
              {v}
              {unit}
            </span>
          )
        })}
      </div>
    </Tooltip>
  ) : (
    <span style={{ color: isRed && 'red' }}>-</span>
  )
}

export const MatchFormat = ({ value, matchOption }) => {
  const title = App.matchOption(matchOption, value).label
  return title ? <Tooltip title={title}>{title}</Tooltip> : '-'
}

export const PureAmountFormat = (value, defaultValue = '-', initFormat = 10000) => {
  return hasValue(value) ? amountFormatFunc(value / initFormat) : defaultValue
}
// 保留几位小数
export function ToFixed({ value, count = 2 }) {
  return (value && (+value).toFixed(count)) ?? null
}

// 金额和金额大写
export const AmountAndCapitalization = ({ value }) => {
  if ([undefined, null, ''].includes(value)) return '-'
  const amountChinese = useMemo(() => {
    const amountStr = `${value}`.replace(/,/g, '')
    return nzhcn.encodeS(amountStr)
  }, [value])
  return (
    <Tooltip title={amountChinese} placement="topLeft">
      <div style={{ width: '100%' }}>{value}</div>
    </Tooltip>
  )
}
