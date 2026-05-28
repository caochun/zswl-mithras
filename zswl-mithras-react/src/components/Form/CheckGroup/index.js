import React, { useMemo } from 'react'
import { Checkbox, Space } from 'antd'
import { observer } from '@zswl/admin'

/**
 * CheckGroup 组件 - 支持全选和半选的复选框组
 * @param {Object} props
 * @param {Array} props.options - 选项数组，格式为 [{label: string, value: string}]
 * @param {Array} props.value - 当前选中的值数组
 * @param {Function} props.onChange - 值改变时的回调函数
 * @param {string} [props.direction='horizontal'] - 排列方向，可选 'horizontal' | 'vertical'
 * @param {number} [props.columnGap=24] - 选项之间的间距
 */
const CheckGroup = ({
  options = [],
  value = [],
  onChange,
  direction = 'horizontal',
  columnGap = 8,
}) => {
  // 计算全选状态
  const checkAll = useMemo(() => {
    return options.length > 0 && value.length === options.length
  }, [value, options])

  // 计算半选状态
  const indeterminate = useMemo(() => {
    return value.length > 0 && value.length < options.length
  }, [value, options])

  // 处理全选事件
  const handleCheckAllChange = (e) => {
    const checked = e.target.checked
    const newValue = checked ? options.map((item) => item.value) : []
    onChange?.(newValue)
  }

  // 处理单个选项变化
  const handleChange = (checkedValue) => {
    onChange?.(checkedValue)
  }

  return (
    <div style={{ display: 'flex' }}>
      <Checkbox
        indeterminate={indeterminate}
        checked={checkAll}
        onChange={handleCheckAllChange}
        style={{ flexShrink: 0 }}
      >
        全选
      </Checkbox>
      <Checkbox.Group value={value} onChange={handleChange}>
        <Space direction={direction} size={columnGap} wrap={direction === 'horizontal'}>
          {options.map((option) => (
            <Checkbox key={option.value} value={option.value}>
              {option.label}
            </Checkbox>
          ))}
        </Space>
      </Checkbox.Group>
    </div>
  )
}

export default observer(CheckGroup)
