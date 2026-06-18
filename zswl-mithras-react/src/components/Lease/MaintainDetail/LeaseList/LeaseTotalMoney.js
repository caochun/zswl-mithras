import { Form } from '@zswl/components'
import { InputNumber, message } from 'antd'
import numeral from 'numeral'
import { create, all } from 'mathjs'
import { hasValue } from '@/utils'
import { useEffect } from 'react'

const mathjs = create(all)
const { Item } = Form
const initFormat = 10000

const Index = ({ value, canEdit, onBlur }) => {
  const [form] = Form.useForm()

  const handleBlur = () => {
    const { totalAmount } = form.getFieldsValue(true)
    if (hasValue(totalAmount)) {
      const newValue = mathjs.multiply(mathjs.bignumber(totalAmount), initFormat).toString()
      onBlur?.(newValue)
    }
  }
  const onChange = (val) => {
    val === null && message.info('请输入租赁物总额')
  }

  useEffect(() => {
    form.setFieldsValue({
      totalAmount: hasValue(value)
        ? mathjs.divide(mathjs.bignumber(value), initFormat).toString()
        : undefined,
    })
  }, [value])

  return (
    <Form layout="horizontal" form={form}>
      <Item label="租赁物总额" name="totalAmount" required>
        <InputNumber
          stringMode
          formatter={(value) => {
            return value && numeral(value).format('0,0.[00]')
          }}
          defaultValue={
            hasValue(value) ? mathjs.divide(mathjs.bignumber(value), initFormat) : undefined
          }
          parser={(value) => value.replace(/\$\s?|(,*)/g, '')}
          disabled={!canEdit}
          addonAfter="元"
          onBlur={handleBlur}
          onChange={onChange}
        />
      </Item>
    </Form>
  )
}

export default Index
