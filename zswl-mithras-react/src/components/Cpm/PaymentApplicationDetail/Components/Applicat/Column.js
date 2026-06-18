import { Input, Tooltip, Space, Radio, Row, Col, InputNumber } from 'antd'

import { Form ,App } from '@zswl/components'
import { cloneElement } from 'react'

import { Select } from '@zswl/components'

import { useEffect } from 'react'
import { getInputNumberAmountProps, hasValue } from '@/utils'
import CommonTips from '@/components/CommonTips'

const { TextArea } = Input
const { Item } = Form

const DownPayment = ({ data }) => {
  const form = Form.useFormInstance()

  useEffect(() => {
    form.setFieldsValue({
      downPaymentType: data.downPaymentType,
    })
  }, [form, data])
  return (
    <Space>
      <FormAmount.Item
        noStyle
        name="downPayment"
        rules={[
          {
            required: true,
            message: '请输入！',
          },
        ]}
      ></FormAmount.Item>
      <Item dependencies={['downPayment']} noStyle>
        {({ getFieldValue }) => {
          const downPaymentVal = getFieldValue('downPayment')
          if (downPaymentVal) {
            return (
              <Item
                name="downPaymentType"
                rules={[
                  {
                    required: true,
                    message: '请选择！',
                  },
                ]}
              >
                <Radio.Group>
                  <Radio value={0}>
                    <Tooltip title="不包含在本次付款中">不包含</Tooltip>
                  </Radio>
                  <Radio value={1}>
                    <Tooltip title="包含在本次付款中">包含</Tooltip>
                  </Radio>
                </Radio.Group>
              </Item>
            )
          }
        }}
      </Item>
    </Space>
  )
}

const FirmEarnest = ({ data }) => {
  const form = Form.useFormInstance()

  useEffect(() => {
    form.setFieldsValue({
      retentionMoneyType: data.retentionMoneyType,
    })
  }, [form, data])
  return (
    <Space>
      <FormAmount.Item
        name="retentionMoney"
        noStyle
        rules={[
          {
            required: true,
            message: '请输入！',
          },
        ]}
      ></FormAmount.Item>

      <Item dependencies={['retentionMoney']} noStyle>
        {({ getFieldValue }) => {
          const retentionMoneyVal = getFieldValue('retentionMoney')
          if (retentionMoneyVal) {
            return (
              <Item
                name="retentionMoneyType"
                rules={[
                  {
                    required: true,
                    message: '请选择！',
                  },
                ]}
              >
                <Radio.Group>
                  <Radio value={1}>
                    <Tooltip title="不内扣在本次付款中">不内扣</Tooltip>
                  </Radio>
                  <Radio value={0}>
                    <Tooltip title="内扣在本次付款中">内扣</Tooltip>
                  </Radio>
                </Radio.Group>
              </Item>
            )
          }
        }}
      </Item>
    </Space>
  )
}

const ALL_COLUMNS = () => {
  return [
    {
      title: '申请付款日期',
      requiredMark: true,
      dataIndex: 'applyPaymentDate',
      editable: (val) =>
        DatePickerEditable(val, 'applyPaymentDate', {
          required: true,
          style: {
            width: 180,
          },
        }),
    },
    {
      title: '申请付款金额(元)',
      requiredMark: true,
      dataIndex: 'applyPaymentAmount',
      editable: (val) =>
        AmountEditable(val, 'applyPaymentAmount', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入申请付款金额！',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '资金拟投放金额(元)',
      dataIndex: 'planPayAmount',
      editable: false,
      span: 1,
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '定价IRR',
      dataIndex: 'pricingIrr',
      editable: false,
      span: 2,
      render: (val) => <AmountFormat value={val} unit="%" />,
    },
    {
      title: '首期租金(元)',
      requiredMark: true,
      dataIndex: 'downPayment',
      span: 1,
      editable: (val) => {
        return <DownPayment data={val}></DownPayment>
      },
      render: (val, record) => {
        return (
          <Space>
            <AmountFormat value={val} />
            {hasValue(record.downPaymentType) && val > 0 ? (
              <span>（{['不包含', '包含'][record.downPaymentType] + '在本次付款中'}）</span>
            ) : null}
          </Space>
        )
      },
    },
    {
      title: '厂商质保金(元)',
      requiredMark: true,
      dataIndex: 'retentionMoney',
      span: 2,
      editable: (val) => {
        return <FirmEarnest data={val}></FirmEarnest>
      },
      render: (val, record) => {
        return (
          <Space>
            <AmountFormat value={val} />
            {hasValue(record.retentionMoneyType) && val > 0 ? (
              <span>（{['内扣', '不内扣'][record.retentionMoneyType] + '在本次付款中'}）</span>
            ) : null}
          </Space>
        )
      },
    },
    {
      title: '手续费/服务费/咨询费(元)',
      dataIndex: 'consultingFee',
      requiredMark: true,
      formTooltip: CommonTips.fieldMapTip['consultingFee'],
      editable: (val) =>
        AmountEditable(val, 'consultingFee', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '服务费/咨询费(元)',
      dataIndex: 'consultingFee',
      formTooltip: CommonTips.fieldMapTip['consultingFee'],
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'consultingFee', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '手续费(元)',
      dataIndex: 'commission',
      requiredMark: true,
      formTooltip: CommonTips.fieldMapTip['commission'],
      editable: (val) =>
        AmountEditable(val, 'commission', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '首期利息(元)',
      dataIndex: 'firstInstallmentInterest',
      requiredMark: true,
      formTooltip: CommonTips.fieldMapTip['firstInstallmentInterest'],
      editable: (val) =>
        AmountEditable(val, 'firstInstallmentInterest', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '最低IRR',
      dataIndex: 'lowestIrr',
      requiredMark: false,
      editable: (val) => {
        const { element, ...rest } = AmountEditable(val, 'lowestIrr', {
          required: false,
          disabled: false,
        })
        return {
          element: cloneElement(element, {
            addonAfter: '%',
          }),
          ...rest,
        }
      },
      render: (val) => {
        return FormAmount.Format({ value: val, suffix: '%' })
      },
    },
    {
      title: '客户保证金(元)',
      dataIndex: 'earnestMoney',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'earnestMoney', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '名义价款(元)',
      dataIndex: 'nominalPrice',
      requiredMark: true,
      // span: 1,
      editable: (val) =>
        AmountEditable(val, 'nominalPrice', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '租赁财产价值',
      dataIndex: 'leasedPrice',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'leasedPrice', {
          required: true,
          disabled: false,
          rules: [
            {
              required: true,
              message: '请输入',
            },
          ],
        }),
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '币种',
      dataIndex: 'leasedCurrency',
      requiredMark: true,
      matchOption: 'paymentCurrencyStatusEnum',
      editable: {
        element: <Select options="paymentCurrencyStatusEnum" />,
        rules: [
          {
            required: true,
            message: '请选择',
          },
        ],
      },
      render: (value, record) => {
        const { leasedCurrency } = record
        const newValue = App.matchOption('paymentCurrencyStatusEnum', leasedCurrency).label
        return newValue || '-'
      },
    },
    {
      title: '备注',
      dataIndex: 'remark',
      // span: 1,
      editable: {
        element: <TextArea allowClear autoSize={{ minRows: 4, maxRows: 20 }} />,
      },
      render: (val) => <FiledFormat title={val} />,
    },
  ]
}
export default ALL_COLUMNS
