import { observer } from '@zswl/admin'
import { App, Form, Input, Select, Table, Upload } from '@zswl/components'
import { Col, Row, TreeSelect } from 'antd'
import { EnterTable } from './BreakDetail'
import { NoEnumFileTable } from '@/components/Table'

const ColItem = ({ children, ...rest }) => {
  return (
    <Col span={8}>
      <Form.Item {...rest}>{children}</Form.Item>
    </Col>
  )
}

const Index = ({ initialValues, store, type = 'break' }) => {
  const { businessList, form, tableData } = store
  const { id: mainId, recordId } = store.page.getParams()
  const isOutbound = type === 'outbound'
  const moduleType = isOutbound ? 'BLACK_GRAY_MANUAL_OUTBOUND' : 'BLACK_GRAY_WAREHOUSE'
  return (
    <Form store={form} cache={false} initialValues={initialValues}>
      <Row gutter={12}>
        <ColItem
          label={'企业名称'}
          name="enterpriseName"
          rules={[{ required: true }]}
          transform={({ label }) => ({
            enterpriseName: label,
          })}
        >
          <Select
            options={store.getEnterpriseName}
            placeholder="请输入企业名称或统一社会信用代码"
            onChange={store.enterpriseNameChange}
            debounceSearch={300}
            labelInValue
          />
        </ColItem>
        <ColItem
          label={'统一社会信用代码'}
          name="unifiedSocialCreditCode"
          rules={[{ required: true }]}
        >
          <Input disabled />
        </ColItem>
        <ColItem
          label={isOutbound ? '业务类型' : '拟开展业务类型'}
          name={isOutbound ? 'businessType' : 'proposedBusinessType'}
          rules={[{ required: true }]}
        >
          <Select
            options={businessList}
            fieldNames={{ label: 'businessDesc', value: 'businessType' }}
            onChange={store.businessTypeChange}
          />
        </ColItem>
      </Row>
      <div className="z-sub-title ">入库信息</div>
      <EnterTable dataSource={tableData} />

      <Row gutter={12} style={{ marginTop: 12 }}>
        <ColItem name="message" label="申请原因描述" rules={[{ required: true }]}>
          <Input.TextArea rows={4} />
        </ColItem>
        {/* <ColItem
          name="applyFileKeys"
          label="申请原因附件"
          transform={(val) => ({ ['applyFileKeys']: val?.map((item) => item.key) })}
        >
          <Upload />
        </ColItem> */}
      </Row>
      <NoEnumFileTable
        title={'申请原因附件'}
        canEdit={mainId || recordId}
        params={{
          mainId,
          moduleType,
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </Form>
  )
}

export default observer(Index)
