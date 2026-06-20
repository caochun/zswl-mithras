
import { InputNumber, message } from 'antd'
import classnames from 'classnames'
import { hasValue } from '@/utils'
import { useEffect, useState, useRef } from 'react'
import { expenseRatioApi as Api } from '@/api/kpi/baseSet/parameterConfigApi'
import styles from './index.less'
import { Button, Form } from '@zswl/components'
import FormListItem from './FormListItem'

const columns = [
  {
    title: '费用计提比例',
    dataIndex: 'expenseRadio',
    editable: {
      element: <InputNumber addonAfter="%" precision={3} />,
      required: true,
    },
    render: (value) => {
      return hasValue(value) && value + '%'
    },
  },
]

const Index = ({ typeInfo }) => {
  const { isEdit } = typeInfo
  const detailData = useRef({})
  const [form] = Form.useForm()
  const getData = async () => {
    const res = await Api.getList()
    detailData.current = res
    form.setFieldValue(
      'configValue',
      res.configValue.map((v) => ({
        ...v,
      }))
    )
  }
  const saveData = async (data) => {
    const { configValue } = await form.validateFields()
    // 根据部门 id 去重
    const departmentList = configValue.reduce((prev, curr) => {
      const { assessDept } = curr
      if (prev.includes(assessDept)) return prev
      return [...prev, assessDept]
    }, [])
    const hasRepeat = departmentList.length !== configValue.length
    if (hasRepeat) {
      message.error('部门不能重复')
      return Promise.reject()
    }
    await Api.saveList({
      configValue,
    })
    message.success('保存成功')
    // await getData()
  }

  useEffect(() => {
    getData()
  }, [])

  return (
    <Form
      form={form}
      initialValues={{ configValue: [{}] }}
      layout="vertical"
      preserve={false}
      autoComplete="off"
    >
      <Form.List name="configValue">
        {(fields, { add, remove }) => {
          return (
            <FormListItem
              fields={fields}
              add={add}
              remove={remove}
              isEdit={isEdit}
              fieldKey={'configValue'}
            />
          )
        }}
      </Form.List>
      {isEdit && (
        <div style={{ display: 'flex', flexDirection: 'row-reverse' }}>
          <Button type="primary" onClick={saveData}>
            确认
          </Button>
        </div>
      )}
    </Form>
  )
}

export default Index
