import approvalControlApi from '@/api/blackGray/approvalControlApi'
import recordTableApi from '@/api/blackGray/recordTableApi'
import { makeAutoObservable, observer } from '@zswl/admin'
import { App, Form, Modal, Select, Table, TableStore } from '@zswl/components'
import { Col, Input, InputNumber, Row, DatePicker, Checkbox } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { getTableColumns, isUnifiedCreditCode, options } from '@/utils'
import queryExternalDataApi from '@/api/blackGray/queryExternalDataApi'
import styles from './styles.less'
import { BlackGrayColumns as ALl_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'
import warehouseRuleApi from '@/api/blackGray/warehouseRuleApi'
import moment from 'moment'
import { saveServer } from '@/utils'

const { periodUnderObservation } = options

const columns = getTableColumns(ALl_COLUMNS, [{ title: '企业名称', rename: '关联企业' }])

class Store {
  constructor({ form }) {
    makeAutoObservable(this)
    this.form = form
  }
  form
  getEnterpriseName = async (searchValue) => {
    const params = {}

    if (!isUnifiedCreditCode(searchValue)) {
      params.enterpriseName = searchValue
    } else {
      params.unifiedSocialCreditCode = searchValue
    }

    return await queryExternalDataApi.postVagueEnterprise(params).then((res) =>
      res.map((item) => ({
        label: item.enterpriseName,
        value: item.unifiedSocialCreditCode,
      }))
    )
  }
  businessList = []
  getBusiness = async () => {
    const res = await recordTableApi.postTypeList({})
    this.businessList = res
  }
  enterpriseName
  tableList = []
  enterpriseNameChange = async (params) => {
    console.log('params: ', params)
    if (!params) {
      this.form.setFieldsValue({
        unifiedSocialCreditCode: undefined,
      })
      return
    }
    const { value, label } = params
    this.form.setFieldsValue({
      unifiedSocialCreditCode: value,
    })
    this.enterpriseName = label
    const { groupEnterpriseName, isAffiliated } =
      await queryExternalDataApi.postAffiliatedEnterprise({
        unifiedSocialCreditCode: value,
        enterpriseName: label,
      })
    this.form.setFieldsValue({
      membershipGroup: groupEnterpriseName,
      blacklistStatus: isAffiliated,
    })
    const res = await queryExternalDataApi.postAssociatedEnterprise({ enterpriseName: label })
    this.tableList = res
    this.table.search()
  }

  businessTypeChange = async (value) => {
    this.form.setFieldValue('applyReasonType', [])
  }
  blackGrayTypeChange = (value) => {
    const data = value === 'BLACK_LIST' ? '12' : '6'
    this.form.setFieldValue('periodUnderObservation', data)
    this.businessTypeChange()
  }
  submit = async (values) => {
    const recordId = await this.save({}, true)
    const { id } = this.page.getParams()
    await approvalControlApi.postWarehouseSubmit({ ...values, id: id ?? recordId })
  }
  table = new TableStore({
    request: async (params) => {
      return this.tableList
    },
  })
}
const ColItem = ({ children, span = 12, ...rest }) => {
  return (
    <Col span={span}>
      <Form.Item {...rest}>{children}</Form.Item>
    </Col>
  )
}
const SingeModal = ({ modal }) => {
  const [form] = Form.useForm()
  const store = useMemo(() => new Store({ form }), [form])
  const [applyReasonOptions, setApplyReasonOptions] = useState([])
  const { orgRolesName } = App.getData().user
  const { enterpriseName } = modal.getInitialValues() ?? {}
  useEffect(() => {
    enterpriseName && store.enterpriseNameChange(enterpriseName)
  }, [enterpriseName])
  const getApplyReasonOptions = async () => {
    const res = await warehouseRuleApi.postConfigList({
      pageSize: 999,
      page: 1,
      source: 'INTERNAL_APPROVAL',
      suitOrg: orgRolesName[0]?.orgCode,
      status: 1,
    })
    // const allInformation = res.list.every((item) =>
    //   item.suitBusiness.includes('INFORMATION_RELATED')
    // )
    // if (allInformation) {
    //   form.setFieldValue('businessType', 'INFORMATION_RELATED')
    // }
    setApplyReasonOptions(res.list)
  }
  useEffect(() => {
    // getBusiness()
    getApplyReasonOptions()
  }, [])
  const close = () => {
    modal.close()
    store.table.setList([])
  }
  return (
    <Modal
      store={modal}
      title="单个录入"
      destroyOnClose
      width={750}
      maskClosable={false}
      onCancel={close}
    >
      <div style={{ height: 500, overflowY: 'scroll', overflowX: 'hidden', paddingRight: 6 }}>
        <Form form={form} column={2} initialValues={{ businessType: 'INFORMATION_RELATED' }}>
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
                debounceSearch={300}
                labelInValue
                onChange={store.enterpriseNameChange}
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
              label={'业务类型'}
              name="businessType"
              rules={[{ required: true }]}
              tooltip={{
                title: (
                  <div>
                    <p>
                      通用业务范国：证券、租赁、汇融、保险黑典当识金控本级除涉信业务、保险业务、二级市场股票、可转债、可交易债券。
                    </p>
                    <p>
                      涉信业务：范画包括证券公司投资业务（非权益类投资)、证券公司的融资业务、租赁公司的所有业务、保险公司投资端（非权益类投资)
                    </p>
                  </div>
                ),
              }}
            >
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
            <Col span={12}>
              <Form.Item dependencies={['businessType', 'blackGrayType']} noStyle>
                {({ getFieldValue }) => {
                  const businessType = getFieldValue('businessType')
                  const blackGrayType = getFieldValue('blackGrayType')
                  const applyOptions = applyReasonOptions?.filter(
                    (item) =>
                      item.suitBusiness?.includes(businessType) &&
                      item.blackGrayType === blackGrayType
                  )
                  return (
                    <Form.Item
                      name="applyReasonType"
                      label="入库原因"
                      rules={[{ required: true }]}
                      className={styles.singeModal}
                      tooltip="需先选择业务类型和黑灰标识"
                      transform={(value) => ({
                        applyReasonType: [value],
                      })}
                    >
                      <Select
                        options={applyOptions}
                        placeholder="请选择申请原因"
                        disabled={!businessType || !blackGrayType}
                        fieldNames={{ label: 'ruleName', value: 'ruleNumber' }}
                        getPopupContainer={() => document.body}
                      />
                    </Form.Item>
                  )
                }}
              </Form.Item>
            </Col>
            <ColItem
              label={'入库日期'}
              name="warehouseTime"
              transform={(val) => ({ warehouseTime: moment(val).format('yyyy-MM-DD') })}
            >
              <DatePicker
                style={{ width: '100%' }}
                disabledDate={(current) => current && current > moment().endOf('day')}
              />
            </ColItem>
            <ColItem
              name={'reportFlag'}
              span={24}
              valuePropName="checked"
              transform={(val) => ({
                reportFlag: val ? 1 : 0,
              })}
            >
              <Checkbox>是否报送金控</Checkbox>
            </ColItem>
            <ColItem
              label={'所属集团'}
              name="membershipGroup"
              tooltip="如为当前企业为一级集团，则所有下属公司均默认进入黑名单"
            >
              <Input disabled />
            </ColItem>
            <Form.Item label="集团黑灰名单" name="blacklistStatus" hidden>
              <Input />
            </Form.Item>
            <Form.Item name={'id'} hidden>
              <Input />
            </Form.Item>
          </Row>

          <Table
            columnsFilter={'mainTask_detail_SingeModal'}
                    onFilter={(key,val) => saveServer('mainTask_detail_SingeModal',val)}
            
            columns={columns}
            store={store.table}
            rowKey="enterpriseName"
            editable={false}
            autoRequest={false}
            columnWidth={200}
            scroll={{ x: 'auto' }}
          />
        </Form>
      </div>
    </Modal>
  )
}

export default observer(SingeModal)
