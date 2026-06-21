import { FormItemContent, StarDom } from '@/components/Form'
import { Col, DatePicker, Descriptions, Form, Input, InputNumber, Row, Tooltip } from 'antd'
import { Select } from '@zswl/components'
import styles from '../index.less'
import { useCallback, useMemo } from 'react'
import { AmountAndCapitalization } from '@/components/Format'

import {
  amountFormat,
  validatorRange,
  getInputNumberAmountProps,
  getInputNumberMonthProps,
  getInputNumberValueFromEvent,
  hasValue,
} from '@/utils'
import { validatorAmount } from '../../../QuotationSchemeShared/utils'
import moment from 'moment'
import useGetMap from '@/utils/hooks/useGetMap'
import CommonTips from '@/components/LeasePricing/FeeTipEntries'

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
const Lease = ({ showValue, form, detail, compareChangeList = [], isLog }) => {
  const { options, getKeyOptionsLabelMap } = useGetMap()
  const getDetailValue = (key) => {
    if (isLog) {
      return detail[key]
    }
    return detail[key]
  }

  const getDetailChange = (key) => {
    if (isLog) {
      return isLog[key]
    }
    if (compareChangeList.length > 0) {
      return compareChangeList.indexOf(key) > -1
    }
    return false
  }
  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }
  const disabledDate = (current) => {
    return current && current < moment().endOf('day')
  }
  const requiredStr = useCallback((val) => {
    return <StarDom name={val} />
  }, [])
  return (
    <>
      <Descriptions
        title="报价方案"
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
                  validatorRange,
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
        label={<StarDom name="项目批复金额" />}
        labelStyle={labelRed(getDetailChange('approvedAmount'))}
      >
        <FormItemContent
          isChange={getDetailChange('approvedAmount')}
          formContent={
            <Form.Item
              name="approvedAmount"
              rules={[
                {
                  required: true,
                  message: '请输入项目批复金额',
                },
                validatorRange,
              ]}
            >
              <InputNumber {...getInputNumberAmountProps()} placeholder="请输入项目批复金额！" />
            </Form.Item>
          }
          value={
            <AmountAndCapitalization
              value={amountFormat(getDetailValue('approvedAmount'))}
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
                    required: true,
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
          label={requiredStr('转让额度有效期(月)')}
          labelStyle={labelRed(getDetailChange('aocCreditTerm'))}
        >
          <FormItemContent
            isChange={getDetailChange('aocCreditTerm')}
            formContent={
              <Form.Item
                name="aocCreditTerm"
                rules={[
                  {
                    required: true,
                    message: '转让额度有效期(月)',
                  },
                ]}
              >
                <InputNumber {...getInputNumberMonthProps()} placeholder="转让额度有效期(月)" />
              </Form.Item>
            }
            value={getDetailValue('aocCreditTerm')}
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
                dependencies={['applyCreditAmount']}
                name="earnestMoney"
                rules={[
                  {
                    required: true,
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
        <Descriptions.Item label={'转让费率'} labelStyle={labelRed(getDetailChange('rateType'))}>
          <FormItemContent
            isChange={getDetailChange('rateType')}
            formContent={
              <Input.Group compact>
                <Form.Item
                  name="rateType"
                  style={{ width: '120px' }}
                  // rules={[
                  //   {
                  //     required: true,
                  //     message: '请选择',
                  //   },
                  // ]}
                >
                  <Select options={options.rateType} placeholder="请选择" />
                </Form.Item>
                <Form.Item
                  name="aocRatePercent"
                  style={{ width: 'calc(100% - 120px)' }}
                  // rules={[
                  //   {
                  //     required: true,
                  //     message: '请输入转让费率',
                  //   },
                  // ]}
                  getValueFromEvent={getInputNumberValueFromEvent}
                >
                  <Input
                    placeholder="请输入转让费率!"
                    suffix={<div className={styles.suffix}>%</div>}
                  />
                </Form.Item>
              </Input.Group>
            }
            value={
              getDetailValue('rateType') &&
              `${getKeyOptionsLabelMap('rateType')[getDetailValue('rateType')]}:${
                hasValue(getDetailValue('aocRatePercent'))
                  ? amountFormat(getDetailValue('aocRatePercent')) + '%'
                  : '-'
              }`
            }
            showValue={showValue}
          />
        </Descriptions.Item>
        <Descriptions.Item
          label={requiredStr('手续费(元)')}
          labelStyle={labelRed(getDetailChange('consultingFee'))}
        >
          <FormItemContent
            isChange={getDetailChange('consultingFee')}
            formContent={
              <Form.Item
                dependencies={['applyCreditAmount']}
                name="consultingFee"
                rules={[
                  {
                    required: true,
                    message: '请输入手续费！',
                  },
                  validatorAmount,
                ]}
              >
                <InputNumber {...getInputNumberAmountProps()} placeholder="请输入手续费！" />
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
          label={requiredStr('还款频率')}
          labelStyle={labelRed(getDetailChange('repayRate'))}
        >
          <FormItemContent
            isChange={getDetailChange('repayRate')}
            formContent={
              <Form.Item name="repayRate" rules={[{ required: true, message: '请选择还款频率' }]}>
                <Select options={options.repayRateEnum} placeholder="请选择还款频率" />
              </Form.Item>
            }
            value={getKeyOptionsLabelMap('repayRateEnum')[getDetailValue('repayRate')]}
            showValue={showValue}
          />
        </Descriptions.Item>
        <Descriptions.Item
          label={
            <StarDom name={<CommonTips fieldName={'interestWay'}> 利息计算方式</CommonTips>} />
          }
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
          label={requiredStr('还款方式')}
          labelStyle={labelRed(getDetailChange('rentalCalcType'))}
        >
          <FormItemContent
            isChange={getDetailChange('rentalCalcType')}
            formContent={
              <Form.Item noStyle dependencies={['interestWay']}>
                {({ getFieldValue, setFieldValue }) => {
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
      </Descriptions>
    </>
  )
}

export default Lease
