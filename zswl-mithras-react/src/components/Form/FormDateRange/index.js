import { amountFormat, formatPercent, hasValue } from '@/utils'
import { Form } from '@zswl/components'
import { DatePicker, InputNumber, Tooltip, Typography } from 'antd'
import _ from 'lodash'
import { useMemo } from 'react'

function FormDateRange({ value = [], onChange, placeholder, ...rest }) {
  const newValue = useMemo(() => (value ?? [])?.map((v) => v && moment(v)), [value])
  const newPlaceholder = `请选择${placeholder ?? '时间'}`
  return (
    <DatePicker.RangePicker
      value={newValue}
      onChange={onChange}
      placeholder={[newPlaceholder, newPlaceholder]}
      style={{ width: '100%' }}
      {...rest}
    />
  )
}

const Item = ({ rules, name, label, style, labelCol, dateFormat = 'yyyy-MM-DD', ...props }) => {
  const format = (val) => val && moment(val).format(dateFormat)
  const transform = (val) => {
    const [startDataTime, endDataTime] = val || []
    return {
      [name]: undefined,
      [`${name}From`]: format(startDataTime),
      [`${name}To`]: format(endDataTime),
    }
  }
  return (
    <Form.Item
      name={name}
      label={label}
      style={style}
      labelCol={labelCol}
      rules={rules}
      transform={transform}
    >
      <FormDateRange {...props} />
    </Form.Item>
  )
}

FormDateRange.Item = Item
FormDateRange.Format = ({ value }) => {
  const isChange = value?.isChange
  return <Tooltip title={title}></Tooltip>
}
export default FormDateRange
