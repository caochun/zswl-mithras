import ReadOnly from '../ReadOnly'
import { App, Select } from '@zswl/components'
import { Input, InputNumber, Radio } from 'antd'

export const InputReadOnly = (props) => {
  const { onlyRead, ...rest } = props
  return onlyRead ? <ReadOnly {...rest} /> : <Input placeholder="请输入" {...rest} />
}

export const TextAreaReadOnly = (props) => {
  const { onlyRead, ...rest } = props
  return onlyRead ? (
    <ReadOnly {...rest} />
  ) : (
    <Input.TextArea placeholder="请输入" allowClear showCount maxLength={2000} {...rest} />
  )
}
export const InputNumberReadOnly = (props) => {
  const { onlyRead, ...rest } = props
  return onlyRead ? <ReadOnly {...rest} /> : <InputNumber placeholder="请输入" {...rest} />
}
export const SelectReadOnly = ({ value, options, onlyRead, ...rest }) => {
  const newValue = App.matchOption(options, value).label
  return onlyRead ? (
    <ReadOnly value={newValue} />
  ) : (
    <Select placeholder="请选择" options={options} value={value} {...rest} />
  )
}

export const RadioReadOnly = ({ value, options, onlyRead, ...rest }) => {
  const newValue = App.matchOption(options, value).label
  return onlyRead ? (
    <ReadOnly value={newValue} />
  ) : (
    <Radio.Group options={options} value={value} {...rest} />
  )
}
