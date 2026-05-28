import {
  AmountEditable,
  AmountAndCapitalization,
  DatePickerEditable,
  TextAreaEditable,
  PureAmountFormat,
} from '@/components/Format'
import { cloneElement } from 'react'
import { hasValue } from '@/utils'
import { Select, Form } from '@zswl/components'
import IconFont from '@/components/Icon'
import mathjs from '@/utils/math'

const ALL_COLUMNS = (props = {}) => {
  const { detail, calcLoss } = props
  return [
    {
      title: '到期未付租金(元)',
      dataIndex: 'outstandingRent',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'outstandingRent', { required: true, disabled: false }),
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '违约金(元)',
      dataIndex: 'liquidatedDamages',
      requiredMark: false,
      formTooltip: '金额取自【收款核销】的罚息余额',
      editable: (val) =>
        AmountEditable(val, 'liquidatedDamages', { required: false, disabled: false }),
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '未到期本金(元)',
      dataIndex: 'beforeMaturityPrincipal',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'beforeMaturityPrincipal', { required: true, disabled: false }),
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '未到期利息(元)',
      dataIndex: 'beforeMaturityInterest',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'beforeMaturityInterest', { required: true, disabled: false }),
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '原到期日',
      dataIndex: 'originalDeadline',
      requiredMark: true,
      editable: (val) =>
        DatePickerEditable(val, 'originalDeadline', { required: true, disabled: false }),
    },
    {
      title: '保证金余额(元)',
      dataIndex: 'earnestBalance',
      requiredMark: false,
      editable: false,
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '保证金是否内扣',
      dataIndex: 'isEarnestDeduction',
      requiredMark: detail.earnestBalance > 0,
      editable: (val) => {
        // if (!hasValue(detail.earnestBalance) || detail.earnestBalance === 0) return false
        return (
          <Form.Item
            name="isEarnestDeduction"
            rules={[
              {
                required: detail.earnestBalance > 0,
                message: '请选择',
              },
            ]}
          >
            <Select options={'yesOrNo'}></Select>
          </Form.Item>
        )
      },
      matchOption: 'yesOrNo',
    },
    {
      title: '名义价款(元)',
      dataIndex: 'nominalPrice',
      editable: (val) => AmountEditable(val, 'nominalPrice', { required: false, disabled: false }),
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '合计金额(元)',
      dataIndex: 'nominalPrice2',
      editable: false,
      render: (
        val,
        {
          outstandingRent,
          beforeMaturityPrincipal,
          liquidatedDamages,
          nominalPrice,
          isEarnestDeduction,
          earnestBalance,
        }
      ) => {
        const value = mathjs.toNonExponential(
          mathjs.format(
            mathjs
              .chain(0)
              .add(outstandingRent ?? 0)
              .add(beforeMaturityPrincipal ?? 0)
              .add(liquidatedDamages ?? 0)
              .add(nominalPrice ?? 0)
              .subtract(isEarnestDeduction === 1 ? earnestBalance ?? 0 : 0)
              .divide(10000)
              .done()
          )
        )
        return <AmountAndCapitalization value={value}></AmountAndCapitalization>
      },
    },
    {
      title: '申请结清日',
      dataIndex: 'applySettleDate',
      requiredMark: true,
      editable: (val) =>
        DatePickerEditable(val, 'applySettleDate', { required: true, disabled: false }),
    },
    {
      title: '提前终止补偿金(元)',
      formTooltip: '金额 = 未到期本金 × 2% / 360 ×（原到期日-申请结清日）',
      dataIndex: 'loss',
      requiredMark: false,
      editable: (val) => {
        const { element, ...rest } = AmountEditable(val, 'loss', {
          required: false,
          disabled: false,
        })
        return {
          element: cloneElement(element, {
            addonAfter: <IconFont type="icon-icon_calculator" onClick={calcLoss} />,
          }),
          ...rest,
        }
      },
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '申请减免金额(元)',
      dataIndex: 'applyDerateAmount',
      editable: (val) =>
        AmountEditable(val, 'applyDerateAmount', { required: false, disabled: false }),
      render: (val) => {
        return <AmountAndCapitalization value={PureAmountFormat(val)}></AmountAndCapitalization>
      },
    },
    {
      title: '合计金额(元)-提前结清',
      dataIndex: 'applyDerateAmount',
      editable: false,
      render: (
        val,
        {
          outstandingRent,
          beforeMaturityPrincipal,
          beforeMaturityInterest,
          loss,
          liquidatedDamages,
          nominalPrice,
          applyDerateAmount,
          earnestBalance,
          isEarnestDeduction,
        }
      ) => {
        const value = mathjs.toNonExponential(
          mathjs.format(
            mathjs
              .chain(0)
              .add(outstandingRent ?? 0)
              .add(beforeMaturityPrincipal ?? 0)
              .add(beforeMaturityInterest ?? 0)
              .add(loss ?? 0)
              .add(liquidatedDamages ?? 0)
              .add(nominalPrice ?? 0)
              .subtract(applyDerateAmount ?? 0)
              .subtract(isEarnestDeduction === 1 ? earnestBalance ?? 0 : 0)
              .divide(10000)
              .done()
          )
        )
        return <AmountAndCapitalization value={value}></AmountAndCapitalization>
      },
    },
    {
      title: '提前结清说明',
      dataIndex: 'settleRemark',
      requiredMark: true,
      span: 2,
      editable: (val) => TextAreaEditable(val, 'settleRemark', { required: true, disabled: false }),
    },
  ]
}

export default ALL_COLUMNS
