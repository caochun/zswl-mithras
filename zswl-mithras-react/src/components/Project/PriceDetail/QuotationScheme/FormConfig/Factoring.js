import { FormItemContent, StarDom } from '@/components/Form'
import { Descriptions, Form, Input, InputNumber, Empty, Tooltip } from 'antd'
import styles from '../index.less'
import { Table, Select, App } from '@zswl/components'
import { useCallback, useMemo } from 'react'
import { observer } from '@zswl/admin'
import {
  amountFormat,
  validatorRange,
  validatorBigZero,
  getInputNumberAmountProps,
  getInputNumberMonthProps,
  getInputNumberValueFromEvent,
  hasValue,
  formatPercent,
} from '@/utils'
import { AmountAndCapitalization } from '@/components/Format'
import { validatorAmount } from '../../../QuotationSchemeShared/utils'
import useGetMap from '@/utils/hooks/useGetMap'
import CommonTips from '@/components/LeasePricing/FeeTipEntries'
import { saveServer } from '@/utils'

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
const Factoring = ({ showValue, form, detail, compareChangeList = [], isLog }) => {
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
  const requiredStr = useCallback((val) => {
    return <StarDom name={val} />
  }, [])
  const columns = useMemo(() => {
    return [
      {
        title: '客户名称',
        dataIndex: 'clientName',
        width: 120,
        fixed: 'left',
        actions({ clientName, clientType, clientId }) {
          return [
            {
              name: (
                <Tooltip title={clientName}>
                  <div className={styles.customerTitle}>{clientName}</div>
                </Tooltip>
              ),
              to: `/customer/maintain/detail/${clientId}?clientType=${clientType}&typeId=create`,
            },
          ]
        },
      },
      {
        title: '客户类型',
        dataIndex: 'clientTypeDisplay',
        width: 100,
      },
      {
        title: '合同编号',
        dataIndex: 'contractNo',
        width: 220,
        render: (item) => {
          if (item) {
            return <>{item}</>
          }
          return '-'
        },
      },
      {
        title: '合同金额',
        dataIndex: 'contractAmount',
        width: 130,
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
        },
      },
      {
        title: '合同到期日',
        width: 140,
        dataIndex: 'contractDueDate',
        render: (v, i) => {
          return (
            <Tooltip title={v}>
              <div className={styles.customerTitle}>{v || '-'}</div>
            </Tooltip>
          )
        },
      },
      {
        title: '状态',
        dataIndex: 'contractStatus',
        width: 120,
        fixed: 'right',
        render: (val) => App.matchOption('contractStatus', val).label,
      },
    ]
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
          label={requiredStr('保理额度有效期(月)')}
          labelStyle={labelRed(getDetailChange('factoringCreditTerm'))}
        >
          <FormItemContent
            isChange={getDetailChange('factoringCreditTerm')}
            formContent={
              <Form.Item
                name="factoringCreditTerm"
                rules={[
                  {
                    required: true,
                    message: '请输入保理额度有效期(月)',
                  },
                  validatorBigZero,
                ]}
              >
                <InputNumber {...getInputNumberMonthProps()} placeholder="请输入保理额度有效期月" />
              </Form.Item>
            }
            value={getDetailValue('factoringCreditTerm')}
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
        <Descriptions.Item
          label={requiredStr('保理融资比例')}
          labelStyle={labelRed(getDetailChange('factoringFinancingProportion'))}
        >
          <FormItemContent
            isChange={getDetailChange('factoringFinancingProportion')}
            formContent={
              <Form.Item
                name="factoringFinancingProportion"
                getValueFromEvent={getInputNumberValueFromEvent}
                rules={[
                  {
                    required: true,
                    message: '请输入保理融资比例!',
                  },
                ]}
              >
                <Input
                  placeholder="请输入保理融资比例!"
                  suffix={<div className={styles.suffix}>%</div>}
                />
              </Form.Item>
            }
            value={
              hasValue(getDetailValue('factoringFinancingProportion')) &&
              amountFormat(getDetailValue('factoringFinancingProportion')) + '%'
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
                rules={[{ required: true, message: '请输入手续费' }, validatorAmount]}
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
          label={requiredStr('保理费率')}
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
                      required: true,
                      message: '请选择',
                    },
                  ]}
                >
                  <Select options={options.rateType} placeholder="请选择！" />
                </Form.Item>
                <Form.Item
                  name="factoringRatePercent"
                  style={{ width: 'calc(100% - 120px)' }}
                  getValueFromEvent={getInputNumberValueFromEvent}
                  rules={[
                    {
                      required: true,
                      message: '请输入保理费率',
                    },
                  ]}
                >
                  <Input suffix={<div className={styles.suffix}>%</div>} />
                </Form.Item>
              </Input.Group>
            }
            // value={
            //   getDetailValue('rateType') &&
            //   `${getKeyOptionsLabelMap('rateType')[getDetailValue('rateType')]}:${
            //     hasValue(getDetailValue('factoringRatePercent'))
            //       ? amountFormat(getDetailValue('factoringRatePercent')) + '%'
            //       : '-'
            //   }`
            // }
            value={
              hasValue(getDetailValue('rateType')) &&
                hasValue(getDetailValue('factoringRatePercent')) ? (
                <div>
                  <span style={{ color: getDetailChange('rateType') ? 'red' : '#333' }}>
                    {getKeyOptionsLabelMap('rateType')[getDetailValue('rateType')]}
                  </span>
                  :
                  <span style={{ color: getDetailChange('factoringRatePercent') ? 'red' : '#333' }}>
                    {amountFormat(getDetailValue('factoringRatePercent'))}%
                  </span>
                </div>
              ) : (
                '-'
              )
            }
            showValue={showValue}
          />
        </Descriptions.Item>
      </Descriptions>
      <>
        <div className={styles.subTitle}>存续租赁合同</div>
        {(isLog ? detail.contracts?.value?.length > 0 : detail.contracts?.length > 0) ? (
          <Table
            columnsFilter={'QuotationScheme_FormConfig_Factoring'}
            onFilter={(key, val) => saveServer('QuotationScheme_FormConfig_Factoring', val)}
            scroll={{ x: 1100 }}
            rowKey={({ clientId, contractId, clientType }) =>
              `${clientId}_${contractId}_${clientType}`
            }
            dataSource={isLog ? detail.contracts?.value || [] : detail.contracts || []}
            columns={columns}
            pagination={true}
          />
        ) : (
          <Empty
            description="债权人/债务人名下无存续租赁合同"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          />
        )}
      </>
    </>
  )
}
export default observer(Factoring)
