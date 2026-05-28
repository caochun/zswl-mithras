import { useState, useMemo, useEffect } from 'react'
import { Form } from '@zswl/components'
import { Input } from 'antd'

const bankCodeReg = /(\d{4})(?=\d)/g

const bankCodePattern = (message = '请输入正确的银行卡号') => {
  // return { pattern: /^[0-9]{6,27}$/, message }
  // 这里格式化以后，有空格存在
  return { pattern: /^.{6,50}$/, message }
}

const BankAccount = (props) => {
  const { onChange, value, ...rest } = props
  const [inputValue, setInputValue] = useState('')
  const formatBankCardNumber = (value) => {
    // 去除非数字字符
    const numericValue = value.replace(/\D/g, '')
    // 将数字每4位分隔
    const formattedValue = numericValue.replace(bankCodeReg, '$1 ')
    return formattedValue
  }
  const handleChange = (e) => {
    const value = e.target.value
    const formattedValue = formatBankCardNumber(value)
    setInputValue(formattedValue)
    onChange?.(formattedValue)
  }
  useEffect(() => {
    if (value) {
      // 处理下原没有格式化的数据
      const newValue = BankAccount.Format({ value })
      setInputValue(newValue)
    }
  }, [value])
  return (
    <Input
      onChange={handleChange}
      onBlur={(e) => {
        // 移除尾部空格
        e.target.value = e.target.value.trim()
      }}
      placeholder="请输入银行卡号"
      {...rest}
      value={inputValue}
    />
  )
}

const Item = ({ name, label, required = true, itemStyle, message, ...props }) => {
  const newRules = useMemo(() => {
    if (!required) return []
    return [{ required: true, message: '请输入' }, bankCodePattern(message)].filter(Boolean)
  }, [message, required])
  return (
    <Form.Item name={name} rules={newRules} label={label} required={required} style={itemStyle}>
      <BankAccount {...props} />
    </Form.Item>
  )
}

BankAccount.Item = Item
BankAccount.rule = bankCodePattern

BankAccount.Format = ({ value }) => {
  const newValue = value?.value !== undefined ? value?.value : value
  // 去除所有的空格
  const trimSpacesValue = newValue?.replace(/\s/g, '')
  if (!trimSpacesValue) return trimSpacesValue
  return trimSpacesValue.replace(bankCodeReg, '$1 ')
}

export default BankAccount
