import { Tooltip } from 'antd'
import numeral from 'numeral'
import { hasValue, getKeyOptionsLabelMapPlus } from '@/utils'

function amountFormat(value) {
  if (!value) {
    return value
  }
  const currentValue = value.toString().replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')
  const arr = currentValue.split('.')
  const numberStr = numeral(arr[0]).format('0,00')
  const SUFFIX = '00'
  const decimalSuffix = arr[1] ? (arr[1] + SUFFIX).slice(0, 2) : SUFFIX
  return numberStr + '.' + decimalSuffix
}

/**
 *
 * @param {* object｜string|number}  data ，渲染的源数值
 * @param {* boolean} isCompare ，数据被包装了(审批流页、日志比对页)
 * @param {* string｜`isConfirm`}  selectEnum，下拉枚举值（all接口的key）,其中 `isConfirm` 为自定义的 是、否
 * @param {* boolean} formatNum, 是否格式化数字,除以10000
 * @param {* function} formatText， 自定义处理数据
 * @param {* formatText}  自定义处理数据
 * @param {* ReactNode} title Tooltip 标题
 * @param {* ReactNode}  children
 * @returns
 */
const RenderColumn = ({
  isCompare,
  selectEnum,
  children,
  data,
  title,
  formatNum,
  formatText = (v) => v,
}) => {
  // tab切换同一个业务模块，审批流数据会讲Store数据污染
  const val = isCompare
    ? data?.value
    : typeof data === 'object' &&
      data !== null &&
      Object.prototype.hasOwnProperty.call(data, 'value')
    ? data?.value
    : data

  let mapVal = val
  // 数字格式化
  if (formatNum) {
    mapVal = hasValue(val) ? amountFormat(val / 10000) : '-'
  } else if (selectEnum) {
    // 下拉值映射
    mapVal =
      selectEnum === 'isConfirm'
        ? (mapVal = ['否', '是'][val])
        : getKeyOptionsLabelMapPlus(selectEnum)[val]
  }
  mapVal = formatText(mapVal)

  return (
    <Tooltip title={hasValue(mapVal) ? title || children || mapVal : '-'} placement="topLeft">
      <span style={{ color: isCompare && data?.isChange ? 'red' : '#333' }}>
        {hasValue(mapVal) ? children || mapVal : '-'}
      </span>
    </Tooltip>
  )
}

export default RenderColumn
