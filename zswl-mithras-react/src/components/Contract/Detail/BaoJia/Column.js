import { Input, Tooltip, Space } from 'antd'
import {
  AmountEditable,
  InputNumberEditable,
  AmountAndCapitalization,
  AmountColumn,
} from '@/components/Format'
import { formatPercent, amountFormat, hasValue } from '@/utils'
import { Select, Form, App } from '@zswl/components'
import IconFont from '@/components/Icon'
import { cloneElement } from 'react'
import YearRate from './YearRate'
import RepayCalcType from '@/components/LeasePricing/RepayCalcTypeEntries'
import StructureInterest from './StructureInterest'

import CommonTips from '@/components/CommonTips'

const Item = Form.Item

const BEFORE_YEARRETE = {
  lprType: { name: 'beforeLprType', disabled: false },
  rateType: { name: 'beforeRateType', disabled: false },
  lprPercent: { name: 'beforeLprPercent', disabled: false },
  lprAddPercent: { name: 'beforeLprAddPercent', disabled: false },
}

const ALL_COLUMNS = ({ handleCalc, isLog, onInterestWayChange = () => {} }) => {
  return [
    {
      title: '合同金额(元)',
      dataIndex: 'applyCreditAmount',
      requiredMark: true,
      editable: (val) => {
        const { element, ...rest } = AmountEditable(val, 'applyCreditAmount', {
          required: true,
          disabled: false,
        })
        return {
          element: cloneElement(element, {
            addonAfter: (
              <div>
                <Tooltip title="根据合同金额/项目金额的比率，自动调整保证金、首期租金、服务费/咨询费">
                  <IconFont type="icon-icon_calculator" onClick={handleCalc} />
                </Tooltip>
              </div>
            ),
          }),
          ...rest,
        }
      },
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    {
      title: '保证金(元)',
      dataIndex: 'earnestMoney',
      requiredMark: true,
      editable: (val) => AmountEditable(val, 'earnestMoney', { required: true, disabled: false }),
      // render: (val) => amountFormat(formatPercent(val)),
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    AmountColumn({
      title: '手续费率',
      dataIndex: 'earnestMoneyRate',
      editable: (val, record) => {
        // 手续费率=服务费/合同金额
        return (
          <Form.Item dependencies={['commission', 'applyCreditAmount']}>
            {({ getFieldValue }) => {
              const consultingFee = getFieldValue('commission')
              const applyCreditAmount = getFieldValue('applyCreditAmount')
              const value = ((consultingFee ?? 0) / (applyCreditAmount ?? 0)) * 100
              return <FormAmount.Format value={value} initFormat={1} precision={1} suffix="%" />
            }}
          </Form.Item>
        )
      },
      render: (val, record) => {
        const consultingFee = record?.commission ?? record?.commission?.value ?? 0
        const applyCreditAmount = record?.applyCreditAmount ?? record?.applyCreditAmount?.value ?? 0
        const value = (consultingFee / applyCreditAmount) * 100
        return <FormAmount.Format value={value} initFormat={1} precision={1} suffix="%" />
      },
    }),
    AmountColumn({
      title: '保证金率',
      dataIndex: 'consultingFeeRate',
      editable: (val, record) => {
        // 保证金率=保证金/合同金额
        return (
          <Form.Item dependencies={['earnestMoney', 'applyCreditAmount']}>
            {({ getFieldValue }) => {
              const earnestMoney = getFieldValue('earnestMoney')
              const applyCreditAmount = getFieldValue('applyCreditAmount')
              const value = ((earnestMoney ?? 0) / (applyCreditAmount ?? 0)) * 100
              return <FormAmount.Format value={value} initFormat={1} precision={1} suffix="%" />
            }}
          </Form.Item>
        )
      },
      render: (val, record) => {
        const earnestMoney = record?.earnestMoney ?? record?.earnestMoney?.value ?? 0
        const applyCreditAmount = record?.applyCreditAmount ?? record?.applyCreditAmount?.value ?? 0
        const value = (earnestMoney / applyCreditAmount) * 100
        return <FormAmount.Format value={value} initFormat={1} precision={1} suffix="%" />
      },
    }),
    {
      title: '租赁期限(月)',
      dataIndex: 'leaseMonthCount',
      requiredMark: true,
      editable: (val) => InputNumberEditable(),
      render: (val) => amountFormat(val),
    },
    {
      title: '首期租金(元)',
      dataIndex: 'downPayment',
      requiredMark: true,
      editable: (val) => AmountEditable(val, 'downPayment', { required: true, disabled: false }),
      // render: (val) => amountFormat(formatPercent(val)),
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    {
      title: '还款频率',
      dataIndex: 'repayRate',
      requiredMark: true,
      matchOption: 'repayRateEnum',
      editable: {
        element: <Select options={'repayRateEnum'} />,
        rules: [{ required: true, message: '请选择' }],
      },
    },
    {
      title: '服务费/咨询费(元)',
      dataIndex: 'consultingFee',
      requiredMark: true,
      formTooltip: CommonTips.fieldMapTip['consultingFee'],
      editable: (val) => AmountEditable(val, 'consultingFee', { required: true, disabled: false }),
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    {
      title: '租赁-手续费(元)',
      dataIndex: 'commission',
      requiredMark: true,
      editable: (val) => AmountEditable(val, 'commission', { required: true, disabled: false }),
      formTooltip: CommonTips.fieldMapTip['commission'],
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    {
      title: '租赁-首期利息(元)',
      dataIndex: 'firstInstallmentInterest',
      formTooltip: CommonTips.fieldMapTip['firstInstallmentInterest'],
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'firstInstallmentInterest', { required: true, disabled: false }),
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    {
      title: '手续费(元)',
      dataIndex: 'consultingFee',
      requiredMark: true,
      editable: (val) => AmountEditable(val, 'consultingFee', { required: true, disabled: false }),
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    {
      title: '还款期数',
      dataIndex: 'repayTimesTotal',
      editable: (val) => InputNumberEditable({ required: false }),
      render: (val) => val,
    },
    {
      title: '名义价款(元)',
      dataIndex: 'nominalPrice',
      requiredMark: true,
      editable: (val) => AmountEditable(val, 'nominalPrice', { required: true, disabled: false }),
      // render: (val) => amountFormat(formatPercent(val)),
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
    {
      title: '支付方式',
      dataIndex: 'payType',
      matchOption: 'payType',
      editable: false,
    },
    {
      title: '租赁利率',
      dataIndex: 'rateType',
      span: 2,
      requiredMark: true,
      editable: (val) => {
        return <YearRate data={val} />
      },
      render: (val, record) => {
        return <YearRate.Detail data={record ?? {}} isLog={isLog} />
      },
    },

    {
      title: '罚息日利率',
      dataIndex: 'defaultInterestRate',
      requiredMark: true,
      editable: (val) => {
        const { element, ...rest } = AmountEditable(val, 'defaultInterestRate', {
          required: true,
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
        return hasValue(val) ? amountFormat(formatPercent(val)) + '%' : '-'
      },
    },
    {
      title: '租前息利率',
      dataIndex: 'beforeLprType',
      span: 2,
      requiredMark: true,
      editable: (val) => {
        return <YearRate data={val} config={BEFORE_YEARRETE} />
      },
      render: (val, record) => {
        return <YearRate.Detail data={record ?? {}} config={BEFORE_YEARRETE} isLog={isLog} />
      },
    },
    {
      title: '额度是否可循环',
      dataIndex: 'creditAmountLoop',
      requiredMark: true,
      matchOption: 'yesOrNo',
      editable: {
        element: <Select options={'yesOrNo'} />,
        rules: [{ required: true, message: '请选择' }],
      },
    },
    {
      title: '保理融资期限(月)',
      dataIndex: 'factoringCreditTerm',
      requiredMark: true,
      editable: (val) => InputNumberEditable(),
      render: (val) => amountFormat(val),
    },
    {
      title: '保理融资比例',
      dataIndex: 'factoringFinancingProportion',
      requiredMark: true,
      editable: (val) => {
        return AmountEditable(val, 'factoringFinancingProportion', {
          required: true,
          disabled: false,
        })
      },
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) + '%' : '-'
      },
    },
    {
      title: '利息计算方式',
      dataIndex: 'interestWay',
      requiredMark: true,
      formTooltip: CommonTips.fieldMapTip['interestWay'],
      matchOption: 'interestWayEnum',
      editable: {
        element: (
          <Select
            options={'interestWayEnum'}
            defaultValue={'ACTUAL_RATE'}
            onChange={onInterestWayChange}
          />
        ),
        rules: [{ required: true, message: '请选择' }],
      },
    },
    {
      title: '结构化利息(元)',
      dataIndex: 'structuredInterestList',
      span: 2,
      editable: (record) => {
        return (
          <Form.Item dependencies={[]} noStyle>
            {({ getFieldValue }) => {
              return (
                <StructureInterest
                  listName="structuredInterestList"
                  value={record?.structuredInterestList ?? []}
                />
              )
            }}
          </Form.Item>
        )
      },
      render: (val) => <StructureInterest.Detail value={val ?? []} />,
    },
    {
      title: '还款方式-租赁',
      dataIndex: 'rentalCalcType',
      requiredMark: true,
      editable: (val) => {
        return <RepayCalcType data={val} fieldName="rentalCalcType" />
      },
      render: (val, record) => {
        return <span>{App.matchOption('repayCalcType', record?.rentalCalcType)?.label}</span>
      },
    },
    {
      title: '还款方式-保理',
      dataIndex: 'repayCalcType',
      requiredMark: true,
      editable: (val) => {
        return <RepayCalcType fieldName="repayCalcType" />
      },
      render: (val, record) => {
        return <span>{App.matchOption('repayCalcType', record?.repayCalcType)?.label}</span>
      },
    },
    {
      title: '保理费率',
      dataIndex: 'rateType',
      span: 2,
      requiredMark: true,
      editable: (val) => {
        return <YearRate data={val} />
      },
      render: (val, record) => {
        return <YearRate.Detail data={record ?? {}} />
      },
    },
    {
      title: '转让额度有期限(月)',
      dataIndex: 'aocCreditTerm',
      requiredMark: true,
      editable: (val) => InputNumberEditable(),
      render: (val) => amountFormat(val),
    },
    {
      title: '转让费率',
      dataIndex: 'rateType',
      span: 2,
      requiredMark: true,
      editable: (val) => {
        return <YearRate data={val} />
      },
      render: (val, record) => {
        return <YearRate.Detail data={record ?? {}} />
      },
    },
    {
      title: '保理合同金额(元)',
      dataIndex: 'contractAmount',
      requiredMark: true,
      editable: (val) => {
        const { element, ...rest } = AmountEditable(val, 'contractAmount', {
          required: true,
          disabled: false,
        })
        return {
          element: cloneElement(element, {
            addonAfter: (
              <div>
                <Tooltip title="根据合同金额/项目金额的比率，自动调整保证金、首期租金、服务费/咨询费">
                  <IconFont type="icon-icon_calculator" onClick={handleCalc} />
                </Tooltip>
              </div>
            ),
          }),
          ...rest,
        }
      },
      // render: (val) => amountFormat(formatPercent(val)),
      render: (val) => {
        return (
          <AmountAndCapitalization
            value={amountFormat(formatPercent(val))}
          ></AmountAndCapitalization>
        )
      },
    },
  ]
}

export default ALL_COLUMNS
