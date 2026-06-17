import FormItemContent from '@/components/FormItemContent'
import { Col, Descriptions, Form, Input, InputNumber } from 'antd'
import { App, Select } from '@zswl/components'
import StarDom from '@/components/StarDom'
import styles from '../index.less'
import { observer } from '@zswl/admin'
import { useCallback } from 'react'
import {
  amountFormat,
  getInputNumberAmountProps,
  getInputNumberMonthProps,
  getInputNumberValueFromEvent,
  hasValue,
} from '@/utils'
import { validatorAmount, validatorBigZero } from './utils'
import useGetMap from '@/utils/hooks/useGetMap'
import { AmountAndCapitalization } from '@/components/Format'
import { CommonTips } from '@/components'

const creditAmountLoopOptions = [
  {
    label: '否',
    value: 0,
  },
  {
    label: '是',
    value: 1,
  },
]
const creditAmountLoopOptionsKeyValue = ['否', '是']
const Lease = ({ showValue, form, detail, noTitle, compareChangeList = [], isLog, isNormal }) => {
  const { options, getKeyOptionsLabelMap } = useGetMap()

  const getDetailValue = (key) => {
    if (isLog) {
      return detail[key]?.value
    }
    return detail[key]
  }
  const getDetailChange = (key) => {
    if (isLog) {
      return detail[key]?.isChange
    }
    if (compareChangeList.length > 0) {
      return compareChangeList.indexOf(key) > -1
    }
    return false
  }
  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }
  const requiredStr = useCallback(
    (val) => {
      return isNormal ? <StarDom name={val} /> : val
    },
    [isNormal]
  )
  return (
    <Descriptions
      title={noTitle ? undefined : '报价方案'}
      bordered
      column={2}
      labelStyle={{ background: '#F5F6FA' }}
      size={'small'}
      className={styles.des}
    >
      <Descriptions.Item
        label={<StarDom name="申报授信金额(元）" />}
        labelStyle={labelRed(getDetailChange('applyCreditAmount'))}
      >
        <FormItemContent
          isChange={getDetailChange('applyCreditAmount')}
          formContent={
            <Form.Item
              name="applyCreditAmount"
              rules={[
                {
                  required: true,
                  message: '请输入申报授信金额(元）',
                },
                validatorBigZero,
              ]}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入申报授信金额！" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('applyCreditAmount'))}
            ></AmountAndCapitalization>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={requiredStr('额度是否可循环')}
        labelStyle={labelRed(getDetailChange('creditAmountLoop'))}
      >
        <FormItemContent
          isChange={getDetailChange('creditAmountLoop')}
          formContent={
            <Form.Item
              name="creditAmountLoop"
              rules={[
                {
                  required: isNormal,
                  message: '请选择额度是否可循环',
                },
              ]}
            >
              <Select options={creditAmountLoopOptions} placeholder="请选择额度是否可循环" />
            </Form.Item>
          }
          value={creditAmountLoopOptionsKeyValue[getDetailValue('creditAmountLoop')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={requiredStr('租赁期限(月)')}
        labelStyle={labelRed(getDetailChange('leaseMonthCount'))}
      >
        <FormItemContent
          isChange={getDetailChange('leaseMonthCount')}
          formContent={
            <Form.Item
              name="leaseMonthCount"
              rules={[
                {
                  required: isNormal,
                  message: '请输入租赁期限(月)',
                },
                validatorBigZero,
              ]}
            >
              <InputNumber {...getInputNumberMonthProps()} placeholder="请输入租赁期限(月)" />
            </Form.Item>
          }
          value={getDetailValue('leaseMonthCount')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={requiredStr('保证金(元)')}
        labelStyle={labelRed(getDetailChange('earnestMoney'))}
      >
        <FormItemContent
          isChange={getDetailChange('earnestMoney')}
          formContent={
            <Form.Item
              name="earnestMoney"
              dependencies={['applyCreditAmount']}
              rules={[
                {
                  required: isNormal,
                  message: '请输入保证金',
                },
                validatorAmount,
              ]}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入保证金" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('earnestMoney'))}
            ></AmountAndCapitalization>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'还款频率'} labelStyle={labelRed(getDetailChange('repayRate'))}>
        <FormItemContent
          isChange={getDetailChange('repayRate')}
          formContent={
            <Form.Item name="repayRate">
              <Select options={options.repayRateEnum} placeholder="请选择还款频率" />
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('repayRateEnum')[getDetailValue('repayRate')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={requiredStr('首期租金(元)')}
        labelStyle={labelRed(getDetailChange('downPayment'))}
      >
        <FormItemContent
          isChange={getDetailChange('downPayment')}
          formContent={
            <Form.Item
              name="downPayment"
              dependencies={['applyCreditAmount']}
              rules={[
                {
                  required: isNormal,
                  message: '请输入首期租金',
                },
                validatorAmount,
              ]}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入首期租金" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('downPayment'))}
            ></AmountAndCapitalization>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'还款期数'}
        labelStyle={labelRed(getDetailChange('repayTimesTotal'))}
      >
        <FormItemContent
          isChange={getDetailChange('repayTimesTotal')}
          formContent={
            <Form.Item name="repayTimesTotal" rules={[validatorBigZero]}>
              <InputNumber {...getInputNumberMonthProps()} placeholder="请输入还款期数" />
            </Form.Item>
          }
          value={getDetailValue('repayTimesTotal')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<CommonTips fieldName={'consultingFee'}>服务费/咨询费(元)</CommonTips>}
        labelStyle={labelRed(getDetailChange('consultingFee'))}
      >
        <FormItemContent
          isChange={getDetailChange('consultingFee')}
          formContent={
            <Form.Item
              dependencies={['applyCreditAmount']}
              name="consultingFee"
              rules={[validatorAmount]}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入服务费/咨询费！" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('consultingFee'))}
            ></AmountAndCapitalization>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<CommonTips fieldName={'commission'}>手续费(元)</CommonTips>}
        labelStyle={labelRed(getDetailChange('commission'))}
      >
        <FormItemContent
          isChange={getDetailChange('commission')}
          formContent={
            <Form.Item
              name="commission"
              rules={[validatorAmount]}
              dependencies={['applyCreditAmount']}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('commission'))}
            ></AmountAndCapitalization>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<CommonTips fieldName={'firstInstallmentInterest'}>首期利息(元)</CommonTips>}
        labelStyle={labelRed(getDetailChange('firstInstallmentInterest'))}
      >
        <FormItemContent
          isChange={getDetailChange('firstInstallmentInterest')}
          formContent={
            <Form.Item
              name="firstInstallmentInterest"
              rules={[validatorAmount]}
              dependencies={['applyCreditAmount']}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('firstInstallmentInterest'))}
            ></AmountAndCapitalization>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'支付方式'} labelStyle={labelRed(getDetailChange('payType'))}>
        <FormItemContent
          isChange={getDetailChange('payType')}
          formContent={
            <Form.Item name="payType">
              <Select options={options.payType} placeholder="请选择支付方式！" />
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('payType')[getDetailValue('payType')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'名义价款(元)'}
        labelStyle={labelRed(getDetailChange('nominalPrice'))}
      >
        <FormItemContent
          isChange={getDetailChange('nominalPrice')}
          formContent={
            <Form.Item
              name="nominalPrice"
              dependencies={['applyCreditAmount']}
              rules={[validatorAmount]}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入名义价款！" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('nominalPrice'))}
            ></AmountAndCapitalization>
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={<CommonTips fieldName={'interestWay'}>利息计算方式</CommonTips>} />}
        labelStyle={labelRed(getDetailChange('interestWay'))}
      >
        <FormItemContent
          isChange={getDetailChange('interestWay')}
          formContent={
            <Form.Item
              name="interestWay"
              rules={[
                {
                  required: true,
                  message: '请选择',
                },
              ]}
            >
              <Select
                options={'interestWayEnum'}
                placeholder="请选择利息计算方式"
                onChange={() => {
                  form.setFieldValue('rentalCalcType', undefined)
                }}
              />
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('interestWayEnum')[getDetailValue('interestWay')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'还款方式'}
        labelStyle={labelRed(getDetailChange('rentalCalcType'))}
      >
        <FormItemContent
          isChange={getDetailChange('rentalCalcType')}
          formContent={
            <Form.Item noStyle dependencies={['interestWay']}>
              {({ getFieldValue, setFieldValue }) => {
                // 重置下
                // 平息法 比实际利率少了 “等额本金”
                const IS_FLAT_RATE = getFieldValue('interestWay') === 'FLAT_RATE'
                const rentalCalcTypeEnum = IS_FLAT_RATE
                  ? options.repayCalcType.filter((item) => item.label !== '等额本金')
                  : options.repayCalcType
                return (
                  <Form.Item name="rentalCalcType">
                    <Select options={rentalCalcTypeEnum} placeholder="请选择还款方式" />
                  </Form.Item>
                )
              }}
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('repayCalcType')[getDetailValue('rentalCalcType')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={requiredStr('租赁利率')}
        labelStyle={labelRed(getDetailChange('rateType'))}
      >
        <FormItemContent
          isChange={getDetailChange('rateType')}
          formContent={
            <Input.Group compact>
              <Form.Item
                name="rateType"
                style={{ width: '120px' }}
                rules={[
                  {
                    required: isNormal,
                    message: '请选择类型',
                  },
                ]}
              >
                <Select options={options.rateType} placeholder="请选择" />
              </Form.Item>
              <Form.Item
                name="leaseRatePercent"
                style={{ width: 'calc(100% - 120px)' }}
                rules={[
                  {
                    required: isNormal,
                    message: '请输入租赁利率',
                  },
                  validatorBigZero,
                ]}
                getValueFromEvent={getInputNumberValueFromEvent}
              >
                <Input
                  placeholder="请输入租赁利率!"
                  suffix={<div className={styles.suffix}>%</div>}
                />
              </Form.Item>
            </Input.Group>
          }
          value={
            hasValue(getDetailValue('rateType')) && hasValue(getDetailValue('leaseRatePercent')) ? (
              <div>
                <span style={{ color: getDetailChange('rateType') ? 'red' : '#333' }}>
                  {getKeyOptionsLabelMap('rateType')[getDetailValue('rateType')]}
                </span>
                :
                <span style={{ color: getDetailChange('leaseRatePercent') ? 'red' : '#333' }}>
                  {amountFormat(getDetailValue('leaseRatePercent'))}%
                </span>
              </div>
            ) : (
              '-'
            )
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={requiredStr('IRR')}
        labelStyle={labelRed(getDetailChange('irrPercent'))}
      >
        <FormItemContent
          isChange={getDetailChange('irrPercent')}
          formContent={
            <Form.Item
              name="irrPercent"
              rules={[
                {
                  required: isNormal,
                  message: '请输入IRR',
                },
              ]}
              getValueFromEvent={getInputNumberValueFromEvent}
            >
              <Input
                placeholder="请输入IRR"
                style={{ width: '37.5%' }}
                suffix={<div className={styles.suffix}>%</div>}
              />
            </Form.Item>
          }
          value={
            hasValue(getDetailValue('irrPercent')) &&
            amountFormat(getDetailValue('irrPercent')) + '%'
          }
          showValue={showValue}
        />
      </Descriptions.Item>
    </Descriptions>
  )
}

export default observer(Lease)
