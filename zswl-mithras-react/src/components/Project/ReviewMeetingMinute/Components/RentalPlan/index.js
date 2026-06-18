
import { App } from '@zswl/components'
import { Descriptions, Form, Input, InputNumber, Radio, Row, Col, Checkbox, Space } from 'antd'
import StarDom from '@/components/StarDom'
import FormListItem from './FormListItem'
import styles from './index.less'
import { observer } from '@zswl/admin'

import { getKeyOptionsLabelMap, projectFinancingRatio } from './utils'
import {
  getInputNumberAmountProps,
  amountFormat,
  formatPercent,
  getInputNumberMonthProps,
} from '@/utils'
const RentalPlan = ({ showValue, form, detail, businessKey }) => {
  const options = App.getData().optionsType
  const getDetailValue = (key) => detail[key]
  const insurancePurchaserDescr = () => {
    if (!getDetailValue('insurancePurchaser')) return '-'
    return (
      <Row style={{ display: 'flex', alignItems: 'flex-start', marginBottom: '8px' }}>
        <Col className={styles.padding5} span={18}>
          <div>{`购买方: ${
            getKeyOptionsLabelMap('projectInsurancePurchaserEnum', options)[
              getDetailValue('insurancePurchaser')
            ]
          }`}</div>
        </Col>
        {getDetailValue('insurancePurchaser') !==
          options.projectInsurancePurchaserEnum[0].value && (
          <>
            {((getDetailValue('policyTypeValue') && getDetailValue('policyRequire') === 'OTHER') ||
              getDetailValue('policyRequire') !== 'OTHER') && (
              <Col className={styles.padding5} span={18}>
                <div>{`险种: ${
                  getDetailValue('policyType') === 'OTHER'
                    ? getDetailValue('policyTypeValue')
                    : getKeyOptionsLabelMap('projectPolicyTypeEnum', options)[getDetailValue('policyType')]
                }`}</div>
              </Col>
            )}
            {((getDetailValue('policyRequireValue') &&
              getDetailValue('policyRequire') === 'OTHER') ||
              getDetailValue('policyRequire') !== 'OTHER') && (
              <Col className={styles.padding5} span={18}>
                <div>{`保险要求: ${
                  getDetailValue('policyRequire') === 'OTHER'
                    ? getDetailValue('policyRequireValue')
                    : getKeyOptionsLabelMap('projectPolicyRequireEnum',options)[
                        getDetailValue('policyRequire')
                      ]
                }`}</div>
              </Col>
            )}
            {['AFTER_LOAN_DISBURSEMENT','BEFORE_LOAN_DISBURSEMENT'].includes(getDetailValue('insurancePurchaseTime')) && (
              <Col className={styles.padding5} span={18}>
                <div>{`保险购买时间: ${
                  getDetailValue('insurancePurchaseTime') === 'AFTER_LOAN_DISBURSEMENT'
                  ? '放款后 '+getDetailValue('insurancePurchaseTimeValue') + ' 个工作日购买' : getDetailValue('insurancePurchaseTime') === 'BEFORE_LOAN_DISBURSEMENT' ? '放款前' : ''
                }`}</div>
              </Col>
            )}
          </>
        )}
      </Row>
    )
  }
  const financingRatioValues = () => {
    if (!getDetailValue('financingRatio') && !getDetailValue('financingRatioValue')) {
      return '-'
    }
    return (
      <Row style={{ display: 'flex', alignItems: 'flex-start', marginBottom: '8px' }}>
        {getDetailValue('financingRatio') !== 'OTHER' && (
          <Col span={18}>
            <span>不超过：</span>
            <FormAmount.Format
              suffix="%"
              value={
                getKeyOptionsLabelMap('projectFinancingRatioEnum',options)[getDetailValue('financingRatio')]
              }
            />
          </Col>
        )}
        {getDetailValue('financingRatio') === 'OTHER' && getDetailValue('financingRatioValue') && (
          <Col span={18}>
            <span>不超过：{getDetailValue('financingRatioValue')}</span>
          </Col>
        )}
      </Row>
    )
  }
  const leaseTypeOnChange = (val) => {
    detail['leaseType'] = val.target.value
  }
  const projectApprovalAmountChange = (val) => {
    const earnestMoneyFlag = getDetailValue('earnestMoneyFlag')
    // 金额
    const projectApprovalAmount = amountFormat(formatPercent(val)) || 0
    // 保证金比例
    const earnestMoneyRatio = amountFormat(formatPercent(getDetailValue('earnestMoneyRatio'))) || 0
    if (earnestMoneyRatio > 0 && earnestMoneyFlag === 1) {
      form.setFieldValue('earnestMoneyAmount', projectApprovalAmount * earnestMoneyRatio * 100)
    }
  }
  return (
    <Descriptions
      dataSource={detail}
      title=""
      bordered
      column={2}
      labelStyle={{ background: '#F5F6FA' }}
      size={'small'}
      className={styles.des}
    >
      <Descriptions.Item span={2} label={<StarDom name={'租赁类型'} />}>
        <FormItemContent
          formDataShow
          formContent={
            <Form.Item name="leaseType" rules={[{ required: true, message: '请选择租赁类型!' }]}>
              <Radio.Group
                onChange={leaseTypeOnChange}
                mode="multiple"
                options={options.leaseType}
              ></Radio.Group>
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('leaseType',options)[getDetailValue('leaseType')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={<StarDom name={'承租人'} />} span={2}>
        <FormItemContent
          formDataShow
          formContent={
            <Form.List name="lesseeInfoDetail">
              {(fields, { add, remove }) => {
                return (
                  <FormListItem
                    detail={detail}
                    form={form}
                    businessKey={businessKey}
                    fields={fields}
                    add={add}
                    required
                    scene="main"
                    remove={remove}
                    addText="添加承租人"
                    fieldKey={'lesseeInfoDetail'}
                    noClientType
                  />
                )
              }}
            </Form.List>
          }
          value={
            getDetailValue('lesseeInfoDetail') ? (
              <FormListItem.Detail values={getDetailValue('lesseeInfoDetail')} />
            ) : (
              '-'
            )
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      {getDetailValue('leaseType') === 'zhi_zu' && (
        <Descriptions.Item label={'供应商'} span={2}>
          <FormItemContent
            formDataShow
            formContent={
              <Form.Item name="supplierInfo">
                <Input disabled placeholder="请输入" maxLength={2500} />
              </Form.Item>
            }
            value={getDetailValue('supplierInfo')}
            showValue={getDetailValue('leaseType') !== 'zhi_zu' || showValue}
          />
        </Descriptions.Item>
      )}
      <Descriptions.Item label={<StarDom name={'租赁物要求'} />} span={2}>
        <FormItemContent
          formDataShow
          formContent={
            <Form.Item
              name="leaseRequire"
              rules={[{ required: true, message: '请输入租赁物要求!' }]}
            >
              <Input placeholder="请输入" maxLength={2500} />
            </Form.Item>
          }
          value={getDetailValue('leaseRequire')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name={'定值依据'} />}>
        <Form.Item
          rules={[{ required: true, message: '请选择定值依据!' }]}
          noStyle
          dependencies={['fixedValueBasis']}
        >
          {({ getFieldValue }) => {
            return (
              <FormItemContent
                noStyle
                formDataShow
                formContent={
                  <>
                    <Form.Item
                      name="fixedValueBasis"
                      rules={[{ required: true, message: '请选择定值依据' }]}
                    >
                      <Radio.Group
                        options={options.projectFixedValueBasisEnum}
                        onChange={(e) => {
                          form.setFieldValue('fixedValueBasisValue', '')
                        }}
                      />
                    </Form.Item>
                    <Form.Item dependencies={['fixedValueBasis']} noStyle>
                      {({ getFieldValue }) => {
                        const fields = getFieldValue('fixedValueBasis')
                        return (
                          <Form.Item
                            name="fixedValueBasisValue"
                            rules={[{ required: true, message: '请输入' }]}
                            style={{
                              paddingLeft: fields === 'OTHER' ? 150 : 0,
                            }}
                          >
                            <Input.TextArea rows={3} placeholder="请输入" />
                          </Form.Item>
                        )
                      }}
                    </Form.Item>
                  </>
                }
                value={
                  <span>
                    {getKeyOptionsLabelMap('projectFixedValueBasisEnum',options)[
                      getDetailValue('fixedValueBasis')
                    ] || getDetailValue('fixedValueBasisValue')
                      ? getKeyOptionsLabelMap('projectFixedValueBasisEnum',options)[
                          getDetailValue('fixedValueBasis')
                        ] +
                        ':' +
                        (getDetailValue('fixedValueBasisValue') ?? '-')
                      : '-'}
                  </span>
                }
                showValue={showValue}
              />
            )
          }}
        </Form.Item>
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name={'保险安排'} />}>
        <Form.Item
          noStyle
          dependencies={['policyType', 'policyRequire', 'insurancePurchaser', 'insurancePurchaseTime']}
          rules={[{ required: true, message: '请选择保险安排!' }]}
        >
          {({ getFieldValue }) => {
            const projSource4 = getFieldValue('policyType')
            const projSource5 = getFieldValue('policyRequire')
            const insurancePurchaseTime = getFieldValue('insurancePurchaseTime')
            const insurancePurchaser = getFieldValue('insurancePurchaser')
            return (
              <FormItemContent
                formDataShow
                formContent={
                  <>
                    <Form.Item
                      label={<StarDom name={'购买方'} />}
                      name="insurancePurchaser"
                      rules={[{ required: true, message: '请选择购买方' }]}
                    >
                      <Radio.Group options={options.projectInsurancePurchaserEnum}></Radio.Group>
                    </Form.Item>
                    {insurancePurchaser !== options.projectInsurancePurchaserEnum[0].value && (
                      <>
                        <Form.Item
                          label={<StarDom name={'险种'} />}
                          name="policyType"
                          rules={[{ required: true, message: '请选择险种' }]}
                        >
                          <Radio.Group options={options.projectPolicyTypeEnum}></Radio.Group>
                        </Form.Item>
                        {projSource4 === 'OTHER' && (
                          <Form.Item
                            name="policyTypeValue"
                            rules={[{ required: true, message: '请输入' }]}
                          >
                            <Input placeholder="请输入" />
                          </Form.Item>
                        )}
                        <Form.Item
                          label={<StarDom name={'保险要求'} />}
                          name="policyRequire"
                          rules={[{ required: true, message: '请选择保险要求' }]}
                        >
                          <Radio.Group options={options.projectPolicyRequireEnum}></Radio.Group>
                        </Form.Item>
                        {projSource5 === 'OTHER' && (
                          <Form.Item
                            name="policyRequireValue"
                            rules={[{ required: true, message: '请输入' }]}
                          >
                            <Input placeholder="请输入" />
                          </Form.Item>
                        )}
                        <Form.Item
                          label={<StarDom name={'保险购买时间'} />}
                          name="insurancePurchaseTime"
                          rules={[{ required: true, message: '请选择保险购买要求' }]}
                        >
                          <Radio.Group options={[{label:'放款前购买',value:'BEFORE_LOAN_DISBURSEMENT'},{label:'放款后',value:'AFTER_LOAN_DISBURSEMENT'}]}></Radio.Group>
                        </Form.Item>
                        {insurancePurchaseTime === 'AFTER_LOAN_DISBURSEMENT' && (
                          <Form.Item
                            name="insurancePurchaseTimeValue"
                            rules={[{ required: true, message: '请输入' }]}
                          >
                            <InputNumber
                              min={0}
                              addonAfter="个工作日购买"
                              {...getInputNumberAmountProps()}
                            />
                          </Form.Item>
                        )}
                      </>
                    )}
                  </>
                }
                value={insurancePurchaserDescr()}
                showValue={showValue}
              />
            )
          }}
        </Form.Item>
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name="授信批复金额(元）" />}>
        <FormItemContent
          formDataShow
          formContent={
            <Form.Item
              name="projectApprovalAmount"
              rules={[{ required: true, message: '请输入授信批复金额(元）' }]}
            >
              <FormAmount
                onChange={projectApprovalAmountChange}
                addonAfter="元"
                step="0.01"
                {...getInputNumberAmountProps()}
                placeholder="请输入授信批复金额！"
              />
            </Form.Item>
          }
          value={
            <FormAmount.Format
              value={getDetailValue('projectApprovalAmount')}
              suffix="元"
            ></FormAmount.Format>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name={'融资比例'} />}>
        <Form.Item
          noStyle
          dependencies={['financingRatio']}
          rules={[{ required: true, message: '请选择融资比例!' }]}
        >
          {({ getFieldValue }) => {
            const financingRatio = getFieldValue('financingRatio')
            return (
              <FormItemContent
                formDataShow
                formContent={
                  <Space>
                    <span>不超过：</span>
                    <Form.Item
                      name="financingRatio"
                      rules={[{ required: true, message: '请选择融资比例!' }]}
                    >
                      <Radio.Group
                        options={projectFinancingRatio(options.projectFinancingRatioEnum)}
                      ></Radio.Group>
                    </Form.Item>
                    {financingRatio === 'OTHER' && (
                      <Form.Item
                        name="financingRatioValue"
                        rules={[{ required: true, message: '请输入' }]}
                      >
                        <FormAmount initFormat={1} addonAfter="%" placeholder="请输入" />
                      </Form.Item>
                    )}
                  </Space>
                }
                value={financingRatioValues()}
                showValue={showValue}
              />
            )
          }}
        </Form.Item>
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name={'租赁期限(月)'} />}>
        <FormItemContent
          formDataShow
          formContent={
            <>
              <Space>
                <span>不超过：</span>
                <Form.Item name="leaseTerm" rules={[{ required: true, message: '请输入' }]}>
                  <InputNumber
                    min={0}
                    addonAfter="月"
                    {...getInputNumberMonthProps()}
                    placeholder="请输入租赁期限(月)"
                  />
                </Form.Item>
              </Space>
              {getDetailValue('leaseType') === 'zhi_zu' && (
                <Checkbox.Group
                  onChange={(vals) => {
                    form.setFieldValue('preLeasePeriodFlag', vals.length > 0 ? 1 : 0)
                  }}
                  defaultValue={[getDetailValue('preLeasePeriodFlag') === 1 ? 1 : 0]}
                  options={[{ label: '含租前期', value: 1 }]}
                />
              )}
            </>
          }
          value={
            getDetailValue('leaseTerm') ? (
              <>
                <div>不超过 {getDetailValue('leaseTerm')}月</div>
                {getDetailValue('leaseType') === 'zhi_zu' && (
                  <div>{getDetailValue('preLeasePeriodFlag') === 1 ? '含租前期' : ''}</div>
                )}
              </>
            ) : (
              '-'
            )
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      {getDetailValue('leaseType') === 'zhi_zu' && (
        <Descriptions.Item span={2} label={<StarDom name={'租前期'} />}>
          <FormItemContent
            formDataShow
            formContent={
              <Form.Item name="preLeasePeriod" rules={[{ required: true, message: '请输入' }]}>
                <FormAmount
                  min={0}
                  step="0"
                  addonAfter="月"
                  {...getInputNumberMonthProps()}
                  placeholder="请输入"
                />
              </Form.Item>
            }
            value={
              <FormAmount.Format
                suffix="月"
                value={getDetailValue('preLeasePeriod')}
              ></FormAmount.Format>
            }
            showValue={getDetailValue('leaseType') !== 'zhi_zu' || showValue}
          />
        </Descriptions.Item>
      )}
      {getDetailValue('leaseType') === 'zhi_zu' && (
        <Descriptions.Item span={2} label={<StarDom name=" 首付款/首期租金(元）" />}>
          <FormItemContent
            formDataShow
            formContent={
              <Form.Item name="downPayment" rules={[{ required: true, message: '请输入' }]}>
                <FormAmount
                  addonAfter="元"
                  step="0.01"
                  min={0}
                  {...getInputNumberAmountProps()}
                  placeholder="请输入"
                />
              </Form.Item>
            }
            value={<FormAmount.Format suffix="元" value={getDetailValue('downPayment')} />}
            showValue={getDetailValue('leaseType') !== 'zhi_zu' || showValue}
          />
        </Descriptions.Item>
      )}
    </Descriptions>
  )
}
export default observer(RentalPlan)
