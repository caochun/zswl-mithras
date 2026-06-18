import { observer } from '@zswl/admin'
import {
  Button,
  Form,
  Input,
  Modal,
  ModalStore,
  Select,
  Table,
  TableStore,
  Upload,
} from '@zswl/components'
import { Card, Cascader, Col, DatePicker, InputNumber, Radio, Row, Space, TreeSelect } from 'antd'
import { getTableColumns, options } from '@/utils'
import { useEffect, useMemo, useState } from 'react'
import ALl_COLUMNS from '../Columns'
import queryExternalDataApi from '@/api/blackGray/queryExternalDataApi'
import { QuestionCircleOutlined } from '@ant-design/icons'
import moment from 'moment'
import { saveServer } from '@/utils'

const { periodUnderObservation } = options

const ColItem = ({ children, ...rest }) => {
  return (
    <Col span={8}>
      <Form.Item {...rest}>{children}</Form.Item>
    </Col>
  )
}
const columns = getTableColumns(ALl_COLUMNS, ['企业名称'])

const Index = ({ store, detail, applyReasonOptions = [] }) => {
  const { businessList, getBusiness, enterpriseName } = store

  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: async () => {
          const res = await queryExternalDataApi.postAssociatedEnterprise({ enterpriseName })
          setTimeout(() => {
            table.setList(res)
          })
        },
      }),
    [enterpriseName]
  )
  const table = useMemo(
    () =>
      new TableStore({
        request: () => {},
      }),
    []
  )

  return (
    <Form store={store.form} initialValues={detail}>
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
            labelInValue
            debounceSearch={300}
          />
        </ColItem>
        <ColItem
          label={'统一社会信用代码'}
          name="unifiedSocialCreditCode"
          rules={[{ required: true }]}
        >
          <Input disabled />
        </ColItem>
        <ColItem label={'业务类型'} name="businessType" rules={[{ required: true }]}>
          <Select options="blackGrayBusinessTypeEnum" onChange={store.businessTypeChange} />
        </ColItem>
        <ColItem label={'黑灰标识'} name="blackGrayType" rules={[{ required: true }]}>
          <Select options="blackGrayTypeEnum" onChange={store.blackGrayTypeChange} />
        </ColItem>
        <ColItem label={'观察期'} name="periodUnderObservation" required>
          <Select options={periodUnderObservation} disabled />
        </ColItem>
        <ColItem
          label={'业务规模（万元）'}
          name={'riskScale'}
          transform={(val) => ({
            riskScale: val,
          })}
        >
          <InputNumber style={{ width: '100%' }} precision={2} />
        </ColItem>

        <Col span={8}>
          <Form.Item dependencies={['businessType', 'blackGrayType']}>
            {({ getFieldValue }) => {
              const businessType = getFieldValue('businessType')
              const blackGrayType = getFieldValue('blackGrayType')
              const applyOptions = applyReasonOptions?.filter(
                (item) =>
                  item.suitBusiness?.includes(businessType) && item.blackGrayType === blackGrayType
              )
              return (
                <Form.Item
                  name="applyReasonType"
                  label="入库原因"
                  rules={[{ required: true }]}
                  tooltip="需先选择业务类型和黑灰标识"
                  transform={(val) => ({
                    applyReasonType: [val],
                  })}
                >
                  <Select
                    options={applyOptions}
                    placeholder="请选择申请原因"
                    disabled={!businessType || !blackGrayType}
                    fieldNames={{ label: 'ruleName', value: 'ruleNumber' }}
                  />
                </Form.Item>
              )
            }}
          </Form.Item>
        </Col>
        <ColItem
          label="入库日期"
          name="warehouseTime"
          rules={[{ required: true }]}
          transform={(val) => ({ warehouseTime: moment(val).format('YYYY-MM-DD') })}
        >
          <DatePicker style={{ width: '100%' }} />
        </ColItem>

        <Col span={8}>
          <Form.Item
            label={'所属集团'}
            name="membershipGroup"
            tooltip={{
              title: '如为当前企业为一级集团，则所有下属公司均默认进入黑名单',
              icon: (
                <Space>
                  <QuestionCircleOutlined />
                  <Button
                    type="link"
                    size="small"
                    onClick={() => modal.open()}
                    disabled={!enterpriseName}
                  >
                    点击查看下属公司清单
                  </Button>
                </Space>
              ),
            }}
          >
            <Input disabled />
          </Form.Item>
        </Col>
        <ColItem name="applyReason" label="申请原因描述" rules={[{ required: true }]}>
          <Input.TextArea rows={4} />
        </ColItem>
        {/* <ColItem
          name="warehouseFileKeys"
          label="申请原因附件"
          transform={(val) => ({ ['warehouseFileKeys']: val?.map((item) => item.key) })}
        >
          <Upload multiple />
        </ColItem> */}
      </Row>
      <NoEnumFileTable
        title={'申请原因附件'}
        canEdit={!!mainId}
        params={{
          mainId,
          moduleType: 'BLACK_GRAY_WAREHOUSE_TASK',
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
      <Modal store={modal} title="查看下属公司清单" footer={null} width={800}>
        <Table
        columnsFilter={'blackListManage_components_EnterForm'}
                onFilter={(key,val) => saveServer('blackListManage_components_EnterForm',val)}
        
          columns={columns}
          store={table}
          rowKey="enterpriseName"
          editable={false}
          columnWidth={200}
          scroll={{ x: 'auto' }}
        />
      </Modal>
    </Form>
  )
}

export default observer(Index)
