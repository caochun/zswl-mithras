import { DateColumn, InputColumn } from '@/components/Format'
import { App, Form, Select } from '@zswl/components'
import { Input, Row, Col, Divider, Space } from 'antd'
import { rules } from '@/utils'
import creditReportApi from '@/api/credit/creditReportApi'
import { PlusOutlined } from '@ant-design/icons'

const ClientInfoDetail = ({ value }) => {
  const list = Array.isArray(value) ? value : []
  if (!list.length) return null
  return (
    <div style={{ width: '100%' }}>
      {list.map((item, idx) => (
        <>
          <Row key={idx} gutter={16} style={{ marginBottom: 8 }}>
            <Col span={12}>
              <div>客户名称：{item?.clientName || '-'}</div>
              <div>统一社会信用代码：{item?.cscCode || '-'}</div>
            </Col>
            <Col span={12}>
              <div>中征码：{item?.zhongZhengCode || '-'}</div>
              <div>
                查询目的：
                {App.matchOption('searchGoalEnum', item?.selectGoal)?.label || '-'}
              </div>
            </Col>
          </Row>
          <Divider />
        </>
      ))}
    </div>
  )
}

const ClientInfoListEditable = ({ creditReportId }) => {
  const form = Form.useFormInstance?.()
  const onClientChange = async (clientId, namePath) => {
    if (!clientId) {
      form?.setFieldValue(namePath.concat(['cscCode']), undefined)
      form?.setFieldValue(namePath.concat(['zhongZhengCode']), undefined)
      form?.setFieldValue(namePath.concat(['clientName']), undefined)
      form?.setFieldValue(namePath.concat(['id']), undefined)
      return
    }
    const res = await creditReportApi.getBaseShowCreditReportByClientId({ clientId })
    form?.setFieldValue(namePath.concat(['clientId']), res?.clientId)
    form?.setFieldValue(namePath.concat(['clientName']), res?.clientName)
    form?.setFieldValue(namePath.concat(['cscCode']), res?.cscCode)
    form?.setFieldValue(namePath.concat(['zhongZhengCode']), res?.zhongZhengCode)
  }

  const getClientOptions = async (clientName) => {
    console.log('creditReportId: ', creditReportId)
    const res = await creditReportApi.getBaseClientInfo({ clientName, creditReportId })
    return res?.map((item) => ({ value: item.clientId, label: item.clientName })) || []
  }
  return (
    <Form.List name="clientInfos" rules={[rules.required()]}>
      {(fields, { add, remove }) => (
        <>
          {fields.map(({ key, name, ...restField }, index) => (
            <>
              <Row key={key} gutter={16} align="middle" style={{ marginBottom: 8 }}>
                <Col span={11}>
                  <Form.Item
                    {...restField}
                    name={[name, 'clientId']}
                    label="客户名称"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select
                      options={getClientOptions}
                      onChange={(v) => onClientChange(v, ['clientInfos', name])}
                    />
                  </Form.Item>
                  {/* 隐藏字段：客户名称用于保存 */}
                  <Form.Item {...restField} name={[name, 'clientName']} hidden>
                    <Input />
                  </Form.Item>
                  <Form.Item {...restField} name={[name, 'id']} hidden>
                    <Input />
                  </Form.Item>
                  <Form.Item {...restField} name={[name, 'cscCode']} label="统一社会信用代码">
                    <Input disabled placeholder="自动反显" />
                  </Form.Item>
                </Col>
                <Col span={10}>
                  <Form.Item {...restField} name={[name, 'zhongZhengCode']} label="中征码">
                    <Input disabled placeholder="自动反显" />
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, 'selectGoal']}
                    label="查询目的"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select options={'searchGoalEnum'} placeholder="请选择" />
                  </Form.Item>
                </Col>
                <Col
                  span={3}
                  style={{
                    display: 'flex',
                    fontSize: 16,
                  }}
                >
                  {index === 0 ? (
                    <Space onClick={() => add()} style={{ color: '#1677ff' }}>
                      <PlusOutlined /> 添加
                    </Space>
                  ) : (
                    <a onClick={() => remove(name)} style={{ color: '#ff4d4f' }}>
                      删除
                    </a>
                  )}
                </Col>
              </Row>
              <Divider />
            </>
          ))}
        </>
      )}
    </Form.List>
  )
}

const ALL_COLUMNS = ({ creditReportId }) => [
  {
    title: '客户信息',
    dataIndex: 'clientInfos',
    span: 2,
    requiredMark: true,
    editable: {
      rules: [rules.required()],
      element: <ClientInfoListEditable creditReportId={creditReportId} />,
    },
    render: (val, record) => {
      const value =
        Array.isArray(val) && val.length
          ? val
          : record?.clientInfos ||
            (record?.clientName
              ? [
                  {
                    clientName: record?.clientName,
                    cscCode: record?.cscCode,
                    zhongZhengCode: record?.zhongZhengCode,
                    selectGoal: record?.selectGoal,
                  },
                ]
              : [])
      return <ClientInfoDetail value={value} />
    },
  },
  DateColumn({
    title: '授权起始日',
    dataIndex: 'authorizationBeganDate',
    width: 180,
    dateFormat: 'YYYY-MM-DD',
    editable: true,
  }),
  InputColumn({ title: '关联项目编号', dataIndex: 'projCode' }),
  InputColumn({ title: '关联项目名称', dataIndex: 'projName' }),
  InputColumn({ title: '查询版本', dataIndex: 'selectVersion' }),
  InputColumn({ title: '信用报告封装格式', dataIndex: 'reportFormat' }),
]

export default ALL_COLUMNS
