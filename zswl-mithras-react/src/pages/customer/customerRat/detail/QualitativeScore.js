import { observer } from '@zswl/admin'
import { App, Descriptions, Form, Table } from '@zswl/components'
import { Checkbox, Col, Collapse, Input, InputNumber, Radio, Row, Space } from 'antd'
import moment from 'moment'
import { useEffect, useState } from 'react'
import { isEmpty, options } from '@/utils'
const { approvalStatus } = options

const { Panel } = Collapse
const labelStyle = {
  // color: 'red',
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
    selectedKey && clearFieldValue(fieldName+'_'+record.value)
  }
  return (
    <div style={{ display:'flex',flexDirection:'column' }}>
      <Radio value={record.value} key={record.value} onClick={onClick}>
        {record.label}
      </Radio>
      {
        record.enumList && record.enumList.length > 0 && selectedKey === record.value &&
        <Form.Item name={fieldName+'_'+record.value} required rules={[{ required: true, message: '请输入' }]}>
          <Radio.Group disabled={disabled} defaultValue={record.fieldValue} style={{ marginLeft: 24 }}>
            <Space direction="vertical">
              {(record.enumList ?? []).map((item) => (
                <Radio value={item.value} key={item.value}>
                  {item.label}
                </Radio>
              ))}
            </Space>
          </Radio.Group>
        </Form.Item>
      }
    </div>
  )
}

export const DynamicFormItem = ({ initialValues, record, needLabel = true, disabled, required = true, clearFieldValue }) => {
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
const Index = ({ initialValues, paramInfo, auth, isFirst, store }) => {
  const { info = {} } = paramInfo ?? {}
  const [infoList, setInfoList] = useState([])
  const [activeKey, setActiveKey] = useState([])
  const formatInfo = (info) => {
    let result = []
    const keyOrder = [
      '持续经营能力',
      '客户背景/规模',
      '财务指标',
      '标的船舶'
    ];
    const keyOrder2 = [
      '持续经营能力（满分27分）',
      '客户背景/规模（30分）',
      '财务指标（15分）',
      '标的船舶（28分）'
    ];
    if(info?.定性指标){
      if(store.model === 'client_hymx'){
        result = keyOrder.map((key, i) => ({ list: info?.定性指标[key] || info?.定性指标[keyOrder2[i]], groupName: key }));
      }else{
        Object.entries(info?.定性指标 ?? {}).forEach(([key, value]) => {
          result.push({ groupName: key, list: value })
        })
      }
    }
    setActiveKey(result.map(({ groupName }) => groupName))
    setInfoList(result)
  }
  useEffect(() => {
    info && formatInfo(info)
  }, [JSON.stringify(info)])
  const clearFieldValue = (key) => {
    store.form.setFieldValue(key, '')
  }
  return (
    <>
      <div style={{ color: '#0058f9', padding: '12px 0' }}>{!!isFirst ? 2 : 1}. 请填写下列指标字段的数值</div>
      <Collapse activeKey={activeKey} onChange={setActiveKey}>
        {infoList.map(({ groupName, list }, index) => (
          <Panel header={groupName} key={groupName}>
            <Row gutter={12}>
              {list.map((item) => (
                <DynamicFormItem initialValues={initialValues} record={item} disabled={!auth} key={item.fieldName} clearFieldValue={clearFieldValue}/>
              ))}
            </Row>
          </Panel>
        ))}
      </Collapse>
    </>
  )
}

export default observer(Index)
