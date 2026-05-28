import { isValidElement, useEffect } from 'react'
import { AmountFormat } from '@/components/Format'
import ArrowUp2 from '@/pages/dashboard/overView/img/arrow_up2.svg'
import ArrowDown2 from '@/pages/dashboard/overView/img/arrow_down2.svg'
import { hasValue } from '@/utils'
import { isFunction, isBoolean } from 'lodash'
import { Tooltip } from 'antd'
import IconFont from '@/components/Icon'
import styles from './index.less'
import { useMemo } from 'react'

// 卡片头部
const HeaderTitle = ({ title, iconType, tipContent }) => {
  return (
    <div className={styles.header}>
      <div className={styles.header_title}>
        {iconType && <IconFont className={styles.header_icon} type={iconType} />}
        {title}
      </div>
      <div className={styles.header_extra}>
        {tipContent && (
          <Tooltip title={tipContent}>
            <IconFont className={styles.icon} type={'icon-icon_info'} />
          </Tooltip>
        )}
      </div>
    </div>
  )
}

// 数值尾部 图标
const SymbolIcon = ({ num, symbolIcon }) => {
  let comp = null
  if (!symbolIcon) return comp
  if (isBoolean(symbolIcon)) {
    if (num > 0) comp = <ArrowUp2 />
    else if (num < 0) comp = <ArrowDown2 />
  } else if (isValidElement(symbolIcon)) {
    comp = symbolIcon
  }
  return <div className={styles.symbolIcon}>{comp}</div>
}

// 每组字段
const Fields = ({ value, unit, label, symbolIcon = false, rowStyle, onClick }) => {
  return (
    <div onClick={() => onClick()} className={styles.row} style={{ ...rowStyle }}>
      {hasValue(value) ? (
        <div className={styles.row_value}>
          <AmountFormat value={value} initFormat={1}></AmountFormat>
          {unit === '%' ? `${unit}` : ''}
          <SymbolIcon symbolIcon={symbolIcon} num={value} />
        </div>
      ) : (
        '-'
      )}
      {label && (
        <div className={styles.row_label}>
          {label}
          {unit?.includes('元') ? `(${unit})` : ''}
        </div>
      )}
    </div>
  )
}

// 卡片组件
/**
 *
 * @param {* Object} data 卡片数据
 * @param {* Object} fieldsConfig 卡片字段映射
 * @param {* Object} contentStyle 卡片样式
 * @param {* Object} rowStyle 卡片内 字段组 样式
 * @param {* Function} onClick 卡片点击事件
 * @returns
 */
const Index = ({ data, fieldsConfig, contentStyle, rowStyle, onClick, openDrawer }) => {
  const { group, iconType, borderColor, tipContent, ...rest } = data

  const hasClickEvent = isFunction(onClick)

  useEffect(() => {
    if (openDrawer) {
      handleRowClick()
    }
  }, [openDrawer])

  const handleRowClick = () => {
    hasClickEvent && onClick(data)
  }

  const fieldsComp = useMemo(() => {
    let current = fieldsConfig
    if (isFunction(fieldsConfig)) {
      current = fieldsConfig({ groupCode: data?.groupCode })
    }

    // 检查是否需要特殊布局（三行布局）
    const needSpecialLayout = [
      'FUND_FINANCE_LOAN_THIS_YEAR',
      'FUND_FINANCE_LOAN_THIS_MONTH',
    ].includes(data?.groupCode)

    if (needSpecialLayout) {
      // 按rowIndex分组字段
      const groupedFields = current?.reduce((acc, item) => {
        const rowIndex = item.rowIndex || 0
        if (!acc[rowIndex]) acc[rowIndex] = []
        acc[rowIndex].push(item)
        return acc
      }, {})

      return Object.keys(groupedFields).map((rowIndex) => (
        <div
          key={rowIndex}
          className={styles.specialRow}
          style={{
            width: '100%',
            display: 'flex',
            justifyContent: rowIndex === '0' ? 'flex-start' : 'space-between',
            marginBottom: '10px',
          }}
        >
          {groupedFields[rowIndex].map((item, index) => (
            <Fields
              key={index}
              unit={rest[item.dataIndex]?.unit}
              label={item.name}
              onClick={() =>
                hasClickEvent && onClick({ ...data, ...item?.searchParam, ...item?.amountParam })
              }
              value={rest[item.dataIndex]?.value ?? rest[item.dataIndex]}
              symbolIcon={item.symbolIcon}
              rowStyle={{
                ...rowStyle,
                width: rowIndex === '0' ? 'auto' : 'calc(50% - 5px)',
              }}
            />
          ))}
        </div>
      ))
    }

    // 原有的布局逻辑
    return current?.map((item) => {
      return (
        <Fields
          unit={rest[item.dataIndex]?.unit}
          label={item.name}
          onClick={() =>
            hasClickEvent && onClick({ ...data, ...item?.searchParam, ...item?.amountParam })
          }
          value={rest[item.dataIndex]?.value ?? rest[item.dataIndex]}
          symbolIcon={item.symbolIcon}
          rowStyle={rowStyle}
        />
      )
    })
  }, [data, fieldsConfig])

  return (
    <div
      className={styles.wrap}
      style={{
        borderTop: `3px solid ${borderColor}`,
        cursor: hasClickEvent ? 'pointer' : 'default',
      }}
    >
      <HeaderTitle title={group} iconType={iconType} tipContent={tipContent} />
      <div
        className={styles.content}
        style={{
          ...contentStyle,
          flexDirection: ['FUND_FINANCE_LOAN_THIS_YEAR', 'FUND_FINANCE_LOAN_THIS_MONTH'].includes(
            data?.groupCode
          )
            ? 'column'
            : 'row',
        }}
      >
        {fieldsComp}
      </div>
    </div>
  )
}

export default Index
