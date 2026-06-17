import { App, Descriptions, Form } from '@zswl/components'
import { Checkbox, Col, DatePicker, Input, InputNumber, Radio, Space } from 'antd'
import moment from 'moment'
import { useState } from 'react'
import { isEmpty, options } from '@/utils'

const { approvalStatus } = options

const labelStyle = {
  background: '#F5F6FA',
  width: 180,
}
const contentStyle = {
  minWidth: 230,
  maxWidth: 320,
}

export const approvalInfoRender = ({ approvalStatusValue, approvalOpinion }) =>
  !isEmpty(approvalStatusValue) ? (
    <Descriptions
      style={{ marginTop: 8 }}
      labelStyle={labelStyle}
      contentStyle={contentStyle}
      items={[
        {
          title: '审核意见',
          dataIndex: 'approvalStatusValue',
          render: (val) => {
            const title = App.matchOption(approvalStatus, val).label
            return <div style={{ color: val ? undefined : 'red' }}>{title}</div>
          },
        },
        { title: '说明', dataIndex: 'approvalOpinion' },
      ]}
      dataSource={{ approvalStatusValue, approvalOpinion }}
    ></Descriptions>
  ) : (
    <></>
  )

const RoRadioGroup = ({ record, disabled, selectedKey, setSelectedKey, clearFieldValue, fieldName }) => {
  const onClick = () => {
    setSelectedKey(record.value)
    selectedKey && clearFieldValue?.(`${fieldName}_${record.value}`)
  }
  return (
    <div style={{ display: 'flex', flexDirection: 'column' }}>
      <Radio value={record.value} key={record.value} onClick={onClick}>
        {record.label}
      </Radio>
      {record.enumList && record.enumList.length > 0 && selectedKey === record.value && (
        <Form.Item
          name={`${fieldName}_${record.value}`}
          required
          rules={[{ required: true, message: '请输入' }]}
        >
          <Radio.Group
            disabled={disabled}
            defaultValue={record.fieldValue}
            style={{ marginLeft: 24 }}
          >
            <Space direction="vertical">
              {(record.enumList ?? []).map((item) => (
                <Radio value={item.value} key={item.value}>
                  {item.label}
                </Radio>
              ))}
            </Space>
          </Radio.Group>
        </Form.Item>
      )}
    </div>
  )
}

export const DynamicFormItem = ({
  initialValues,
  record,
  needLabel = true,
  disabled,
  required = true,
  clearFieldValue,
}) => {
  const {
    dataType,
    fieldName,
    fieldComment,
    enumList,
    approvalStatus: approvalStatusValue,
    approvalOpinion,
  } = record
  const commonProps = {
    label: needLabel ? fieldComment : undefined,
    name: fieldName,
    required,
    rules: required ? [{ required: true, message: '请输入' }] : [],
  }
  const [selectedKey, setSelectedKey] = useState(initialValues?.[fieldName])
  const approvalInfo = approvalInfoRender({ approvalStatusValue, approvalOpinion })
  if (['date', 'time'].includes(dataType)) {
    const format = dataType === 'date' ? 'YYYY-MM-DD' : 'YYYY-MM-DD HH:mm:ss'
    return (
      <Col span={8}>
        <Form.Item {...commonProps} transform={(val) => val && moment(val).format(format)}>
          <DatePicker
            format={format}
            showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
            disabled={disabled}
          />
        </Form.Item>
        {approvalInfo}
      </Col>
    )
  }
  if (dataType === 'enum') {
    return (
      <Col span={24}>
        <Form.Item {...commonProps}>
          <Radio.Group disabled={disabled}>
            <Space direction="vertical">
              {(enumList ?? []).map((item) => (
                <RoRadioGroup
                  record={item}
                  disabled={disabled}
                  selectedKey={selectedKey}
                  setSelectedKey={setSelectedKey}
                  clearFieldValue={clearFieldValue}
                  fieldName={fieldName}
                  key={item.value}
                />
              ))}
            </Space>
          </Radio.Group>
        </Form.Item>
        {approvalInfo}
      </Col>
    )
  }
  if (dataType === 'enum_collection') {
    return (
      <Col span={24}>
        <Form.Item {...commonProps}>
          <Checkbox.Group disabled={disabled}>
            <Space direction="vertical">
              {(enumList ?? []).map((item) => (
                <Checkbox value={item.value} key={item.value}>
                  {item.label}
                </Checkbox>
              ))}
            </Space>
          </Checkbox.Group>
        </Form.Item>
        {approvalInfo}
      </Col>
    )
  }
  if (dataType === 'number') {
    return (
      <Col span={8}>
        <Form.Item {...commonProps}>
          <InputNumber
            disabled={disabled}
            addonAfter={record?.unit ?? '万元'}
            style={{ minWidth: 200 }}
          />
        </Form.Item>
        {approvalInfo}
      </Col>
    )
  }

  return (
    <Col span={8}>
      <Form.Item {...commonProps}>
        <Input disabled={disabled} />
      </Form.Item>
      {approvalInfo}
    </Col>
  )
}
