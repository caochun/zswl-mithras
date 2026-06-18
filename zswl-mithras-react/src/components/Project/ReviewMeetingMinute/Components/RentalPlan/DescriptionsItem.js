
import { App } from '@zswl/components'
import { Descriptions, Form, Input, Radio, Row, Col } from 'antd'
import StarDom from '@/components/StarDom'
import FormListItem from './FormListItem'
import styles from './index.less'
import { observer } from '@zswl/admin'
import { getInputNumberAmountProps, amountFormat, formatPercent, getInputNumberValueFromEvent } from '@/utils'

const IndexItem = ({ showValue, form, detail, businessKey }) => {
  const options = App.getData().optionsType
  const getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (options && options[key]) {
      options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  const getDetailValue = (key) => {
    return detail[key]
  }
  const getDetailChange = (key) => {
    return false
  }
  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }
  const earnestMoneyRatioChange = (val, projectApprovalAmount) => {
    if (val && projectApprovalAmount) {
      form.setFieldValue('earnestMoneyAmount', formatPercent(projectApprovalAmount) * formatPercent(val) * 100)
    } else {
      form.setFieldValue('earnestMoneyAmount', 0)
      form.setFieldValue('earnestMoneyRatio', 0)
    }
  }
  const earnestMoneyFlagDescr = (earnestMoneyFlag) => {
    if (!getDetailValue('earnestMoneyFlag') && !getDetailValue('earnestMoneyCollectType')) {
      return '-'
    }
    return (
      <Row style={{ display: 'flex', alignItems: 'flex-start' }}>
        <Col className={styles.padding5} span={18}>
          <div>{`保险金: ${getDetailValue('earnestMoneyFlag') === 0 ? '无' : '有'}`}</div>
        </Col>
        {
          getDetailValue('earnestMoneyFlag') === 1 &&
          <>
            {
              <Col className={styles.padding5} span={18}>
                <div>{`比例: ${amountFormat(formatPercent(getDetailValue('earnestMoneyRatio')))||0}%  金额:${formatPercent(getDetailValue('earnestMoneyAmount'))||0}元`}</div>
              </Col>
            }
            {
              getDetailValue('earnestMoneyCollectType')&&
              getDetailValue('earnestMoneyCollectType') === 'OTHER'&&
              getDetailValue('earnestMoneyCollectTypeValue')&&
              <Col className={styles.padding5} span={18}>
                <div>{`其他：${getDetailValue('earnestMoneyCollectTypeValue') }`}</div>
              </Col>
            }
            {
              getDetailValue('earnestMoneyCollectType')&&
              getDetailValue('earnestMoneyCollectType') !== 'OTHER'&& 
              <Col className={styles.padding5} span={18}>
                <div>{getKeyOptionsLabelMap('earnestMoneyCollectTypeEnum')[getDetailValue('earnestMoneyCollectType')]}</div>
              </Col>
            }
          </>
        }
      </Row>
    )
  }
  return (
    <Descriptions dataSource={detail} title="" bordered column={2} labelStyle={{ background: '#F5F6FA' }} size={'small'} className={styles.desItem} >
      <Descriptions.Item span={2} label={<StarDom name={'保证金'} />} labelStyle={labelRed(getDetailChange('earnestMoneyFlag'))}>
        <Form.Item
          noStyle
          dependencies={['earnestMoneyFlag', 'earnestMoneyCollectType', 'earnestMoneyRatio', 'projectApprovalAmount']}
          rules={[{ required: true, message: '请选择保证金!' }]}
        >
          {({ getFieldValue }) => {
            const earnestMoneyFlag = getFieldValue('earnestMoneyFlag')
            const earnestMoneyCollectType = getFieldValue('earnestMoneyCollectType')
            const projectApprovalAmount = getFieldValue('projectApprovalAmount')
            const earnestMoneyRatio = getFieldValue('earnestMoneyRatio')
            return (
              <FormItemContent
              formDataShow
                isChange={getDetailChange('earnestMoneyFlag')}
                formContent={
                  <>
                    <Form.Item className={styles.padding5} name="earnestMoneyFlag" rules={[{ required: true, message: '请选择!' }]}>
                      {/* onChange={earnestMoneyFlagOnChange} */}
                      <Radio.Group options={[{ label: '无', value: 0 }, { label: '有', value: 1 },]}></Radio.Group>
                    </Form.Item>
                    {
                      earnestMoneyFlag === 1 &&
                      <>
                        <Form.Item rules={[{ required: true, message: '请输入!' }]} name="earnestMoneyRatio" style={{ width: '100' }} >
                          <FormAmount onChange={(val) => earnestMoneyRatioChange(val, projectApprovalAmount)} max={100} min={0} addonAfter="%" precision={2} step="0"></FormAmount>
                        </Form.Item>
                        <Form.Item name="earnestMoneyAmount" style={{ width: '100' }} >
                          <FormAmount disabled addonAfter="元"></FormAmount>
                        </Form.Item>
                        <div  className={styles.earnestMoneyFlag}></div>
                        <Form.Item  name="earnestMoneyCollectType" rules={[{ required: true, message: '请选择!' }]}>
                          <Radio.Group className={styles.baoxian} options={options.earnestMoneyCollectTypeEnum}></Radio.Group>
                        </Form.Item>
                        {earnestMoneyCollectType === 'OTHER' &&
                          <Form.Item
                            name="earnestMoneyCollectTypeValue"
                            rules={[{ required: true, message: '请输入!' }]}
                            style={{ width: 'calc(100% - 120px)' }}
                            getValueFromEvent={getInputNumberValueFromEvent}
                          >
                            <Input
                              placeholder="请输入"
                            />
                          </Form.Item>
                        }
                      </>
                    }
                  </>
                }
                value={earnestMoneyFlagDescr()}
                showValue={showValue}
              />
            )
          }}
        </Form.Item>
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name={'租金支付方式'} />} labelStyle={labelRed(getDetailChange('rentPaymentMethodType'))}>
        <FormItemContent
        formDataShow
          isChange={getDetailChange('rentPaymentMethodType')}
          formContent={
            <Form.Item
              name="rentPaymentMethodType"
              rules={[{ required: true, message: '请选择租金支付方式!' }]}
            >
              <Radio.Group
                options={options.rentPaymentMethodTypeEnum}
              ></Radio.Group>
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('rentPaymentMethodTypeEnum')[getDetailValue('rentPaymentMethodType')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name={'租金支付频率'} />} labelStyle={labelRed(getDetailChange('rentPaymentMethodRate'))}>
        <FormItemContent
        formDataShow
          isChange={getDetailChange('rentPaymentMethodRate')}
          formContent={
            <Form.Item
              name="rentPaymentMethodRate"
              rules={[{ required: true, message: '请选择租金支付频率!' }]}
            >
              <Radio.Group
                options={options.rentPaymentMethodRateEnum}
              ></Radio.Group>
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('rentPaymentMethodRateEnum')[getDetailValue('rentPaymentMethodRate')]}
          showValue={showValue}
        />
      </Descriptions.Item>

      <Descriptions.Item span={2} label={<StarDom name={'融资款支付方式'} />} labelStyle={labelRed(getDetailChange('financingFundMethodType'))}>
      <Form.Item dependencies={['financingFundMethodType']}>
      {({ getFieldValue }) => {
            const financingFundMethodType = getFieldValue('financingFundMethodType')

        return (<FormItemContent
        formDataShow
          isChange={getDetailChange('financingFundMethodType')}
          formContent={
            <>
              <Form.Item
                name="financingFundMethodType"
                rules={[{ required: true, message: '请选择融资款支付方式!' }]}
              >
                <Radio.Group
                  style={{ marginBottom: 10 }}
                  options={options.financingFundMethodTypeEnum}
                ></Radio.Group>
              </Form.Item>
              {
                financingFundMethodType === options.financingFundMethodTypeEnum[1].value &&
                <Form.Item
                  name="financingFundSuppleRemark"
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <Input  placeholder="请输入" maxLength={2500} />
                </Form.Item>
              }
            </>
          }
          value={
            getDetailValue('financingFundMethodType') === options.financingFundMethodTypeEnum[1].value ?
            <>
              <div className={styles.padding5}>{getKeyOptionsLabelMap('financingFundMethodTypeEnum')[getDetailValue('financingFundMethodType')]}</div>
              {
                getDetailValue('financingFundSuppleRemark') &&
                <div className={styles.padding5}>{getDetailValue('financingFundSuppleRemark')}</div>
              }
            </>
            :getKeyOptionsLabelMap('financingFundMethodTypeEnum')[getDetailValue('financingFundMethodType')] 
          }
          showValue={showValue}
        />)
      }}
      </Form.Item>

      </Descriptions.Item>
      <Descriptions.Item label={<StarDom name={'资金用途'} />} span={2} labelStyle={labelRed(getDetailChange('fundsPurpose'))} >
        <FormItemContent
        formDataShow
          isChange={getDetailChange('fundsPurpose')}
          formContent={
            <Form.Item name="fundsPurpose" rules={[{ required: true, message: '请输入!' }]}>
              <Input placeholder="请输入" maxLength={2500} />
            </Form.Item>
          }
          value={getDetailValue('fundsPurpose')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item span={2} label={<StarDom name={'起租方式'} />} labelStyle={labelRed(getDetailChange('rentalStartMethod'))}>
        <FormItemContent
        formDataShow
          isChange={getDetailChange('rentalStartMethod')}
          formContent={
            <Form.Item
              name="rentalStartMethod"
              rules={[{ required: true, message: '请选择起租方式!' }]}
            >
              <Radio.Group
                style={{ marginBottom: 10 }}
                // defaultValue={options.rentalStartMethodEnum[0].value}
                options={options.rentalStartMethodEnum}
              ></Radio.Group>
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('rentalStartMethodEnum')[getDetailValue('rentalStartMethod')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        span={2}
        label={'起租条件'}
        labelStyle={labelRed(getDetailChange('rentalStartCondition'))}
      >
        <FormItemContent
        formDataShow
          isChange={getDetailChange('rentalStartCondition')}
          formContent={
            <Form.Item name="rentalStartCondition">
              <Input placeholder="请输入" maxLength={2500} />
            </Form.Item>
          }
          value={getDetailValue('rentalStartCondition')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        span={2}
        label={<StarDom name="名义价款" />}
        labelStyle={labelRed(getDetailChange('nominalPrice'))}
      >
        <FormItemContent
        formDataShow
          isChange={getDetailChange('nominalPrice')}
          formContent={
            <Form.Item name="nominalPrice" rules={[{ required: true, message: '请输入' }]} >
              <FormAmount addonAfter="元"  {...getInputNumberAmountProps()} placeholder="请输入" />
            </Form.Item>
          }
          value={
            <FormAmount.Format
              suffix="元"
              value={getDetailValue('nominalPrice')}
            ></FormAmount.Format>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'决议/批准文件'} />}
        span={2}
        labelStyle={labelRed(getDetailChange('resolutionInfoDetails'))}
      >
        <FormItemContent
        formDataShow
          isChange={getDetailChange('resolutionInfoDetails')}
          formContent={
            <>
              <Form.List
                name="resolutionInfoDetails"
              >
                {(fields, { add, remove }) => {
                  return (
                    <FormListItem detail={detail}  businessKey={businessKey} fields={fields} add={add} form={form} required={true} remove={remove} scene="main" addText="添加" fieldKey={'resolutionInfoDetails'} noClientType />
                  )
                }}
              </Form.List>
              <Form.List name="resolutionInfoDetails" >
                {(fields, { add, remove }) => {
                  return (
                    <>
                    <Form.Item rules={[{ required: (getDetailValue('resolutionInfoDetails')&&getDetailValue('resolutionInfoDetails').length > 1)?false:true, message: '请输入!' }]}  key={0} style={{ margin: ' 5px 0 20px 0' }} label={'其他'} name={[0, 'otherMessage']} >
                      <Input placeholder="请输入" maxLength={2500} />
                    </Form.Item>
                    </>
                  )
                }}
              </Form.List>
            </>
          }
          value={getDetailValue('resolutionInfoDetails') ? <FormListItem.Detail fieldKey={'resolutionInfoDetails'} values={getDetailValue('resolutionInfoDetails')} /> : '-'}
          showValue={showValue}
        />
      </Descriptions.Item>
    </Descriptions>
  )
}
export default observer(IndexItem)