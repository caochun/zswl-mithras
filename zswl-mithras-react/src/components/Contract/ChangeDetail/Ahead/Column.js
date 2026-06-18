import {
  DateColumn,
  AmountColumn,
  TextAreaColumn,
  MatchOptionColumn,
  DatePickerEditable,
} from '@/components/Format'
import { Space } from 'antd'

import { Form, Select, App } from '@zswl/components'
import mathjs from '@/utils/math'
import { LossItem, PenaltyItem, EarnestMoneyItem, PenaltyRender, LossRender } from './RemitForm'

const { Item } = Form

export const ALL_COLUMNS = (store, isStartUserModify) => {
  return [
    {
      title: '提前还款日',
      dataIndex: 'applayRepaymentDate',
      requiredMark: true,
      rules: [{ required: true, message: '请选择' }],
      editable: (val) =>
        DatePickerEditable(val, 'applayRepaymentDate', {
          disabled: false,
          required: false,
          onChange: store.onApplayRepaymentDateChange,
        }),
    },
    // DateColumn({
    //   title: '提前还款日',
    //   dataIndex: 'applayRepaymentDate',
    //   requiredMark: true,
    //   rules: [{ required: true, message: '请选择' }],
    //   editable: true,
    // }),
    MatchOptionColumn({
      title: '是否提前结清',
      dataIndex: 'isEarlySettle',
      requiredMark: true,
      matchOption: 'yesOrNo',
      editable: isStartUserModify
        ? false
        : () => {
            return <Select options={'yesOrNo'} onChange={store.onIsEarlySettleChange}></Select>
          },
    }),
    AmountColumn({
      title: '到期未付租金(元)',
      dataIndex: 'unpaidRentDue',
      editable: false,
      span: 2,
    }),
    AmountColumn({
      title: '违约金(元)',
      dataIndex: 'penaltyRow',
      span: 2,
      editable: isStartUserModify
        ? false
        : (record) => {
            return <PenaltyItem store={store} data={record}></PenaltyItem>
          },
      render: (_, record) => <PenaltyRender data={record}></PenaltyRender>,
    }),
    AmountColumn({
      title: '提前归还本金(元)',
      dataIndex: 'earlyRepayment',
      requiredMark: true,
      editable: !isStartUserModify,
    }),
    AmountColumn({
      title: '提前归还利息(元)',
      dataIndex: 'earlyRepaymentInterest',
      // editable: false,
      editable: !isStartUserModify,
      wrapItemProps: {
        disabled: true,
      },
    }),
    AmountColumn({
      title: '未到期本金(元)',
      dataIndex: 'beforeMaturityPrincipal',
      // requiredMark: true,
      editable: !isStartUserModify,
      wrapItemProps: {
        disabled: true,
      },
    }),
    AmountColumn({
      title: '未到期利息(元)',
      dataIndex: 'beforeMaturityInterest',
      // requiredMark: true,
      // editable: false,
      editable: !isStartUserModify,
      wrapItemProps: {
        disabled: true,
      },
    }),
    {
      title: '提前终止补偿金(元)',
      dataIndex: 'lossRow',
      span: 2,
      editable: !isStartUserModify,
      formTooltip: '金额 = 未到期本金（或提前归还本金） × 2% / 360 ×（原到期日-提前还款日）',
      editable: isStartUserModify
        ? false
        : (record) => {
            return <LossItem store={store} data={record}></LossItem>
          },
      render: (_, record) => <LossRender data={record}></LossRender>,
    },
    AmountColumn({
      title: '保证金余额(元)',
      dataIndex: 'earnestMoneyBalance',
      editable: !isStartUserModify,
      wrapItemProps: {
        disabled: true,
      },
    }),
    AmountColumn({
      title: '名义价款(元)',
      dataIndex: 'nominalPrice',
      requiredMark: true,
      editable: !isStartUserModify,
      required: true,
      rules: [{ required: true, message: '请输入' }],
    }),
    AmountColumn({
      title: '合计金额(元)',
      dataIndex: 'totalAmount',
      editable: false,
      render: (
        _,
        {
          unpaidRentDue,
          earlyRepayment,
          earlyRepaymentInterest,
          earnestMoneyDeductionAmount,
          nominalPrice,
          beforeMaturityPrincipal,
          beforeMaturityInterest,

          isEarlySettle,

          penalty,
          penaltyDerateType,
          penaltyDerateAmount,

          loss,
          lossDerateType,
          applyDerateAmount,
        }
      ) => {
        let diffLossMap = {
          NONE: loss,
          ALL: loss - applyDerateAmount,
          PERCENT: loss - applyDerateAmount,
          FIXED: loss - applyDerateAmount,
        }[lossDerateType]

        let diffPenaltyMap = {
          NONE: penalty,
          ALL: penalty - penaltyDerateAmount,
          PERCENT: penalty - penaltyDerateAmount,
          FIXED: penalty - penaltyDerateAmount,
        }[penaltyDerateType]

        if (isEarlySettle) {
          //   - 合计金额=到期未付租金+违约金+未到期本金+提前终止补偿金-保证金抵扣金额+名义价款
          return mathjs.toNonExponential(
            mathjs.format(
              mathjs
                .chain(unpaidRentDue || 0)
                .add(diffPenaltyMap || 0)
                .add(beforeMaturityPrincipal || 0)
                .add(beforeMaturityInterest || 0)
                .add(diffLossMap || 0)
                .subtract(earnestMoneyDeductionAmount || 0)
                .add(nominalPrice || 0)
                .divide(10000)
                .done()
            )
          )
        }
        //   - 合计金额=到期未付租金+违约金+提前归还本金+提前归还利息+提前终止补偿金-保证金抵扣金额（默认为0）+名义价款
        return mathjs.toNonExponential(
          mathjs.format(
            mathjs
              .chain(unpaidRentDue || 0)
              .add(diffPenaltyMap || 0)
              .add(earlyRepayment || 0)
              .add(earlyRepaymentInterest || 0)
              .add(diffLossMap || 0)
              // .subtract(earnestMoneyDeductionAmount || 0)
              .add(nominalPrice || 0)
              .divide(10000)
              .done()
          )
        )
      },
    }),
    TextAreaColumn({
      title: '提前还款说明',
      dataIndex: 'remark',
      requiredMark: store.isEarlySettle === 1,
      required: store.isEarlySettle === 1,
      editable: !isStartUserModify,
    }),
    AmountColumn({
      title: '保证金抵扣金额(元)',
      dataIndex: 'earnestMoneyDeductionRow',
      requiredMark: true,
      rules: [{ required: true, message: '请输入' }],
      editable: isStartUserModify
        ? false
        : (record) => {
            return (
              <EarnestMoneyItem
                data={record}
                isStartUserModify={isStartUserModify}
              ></EarnestMoneyItem>
            )
          },
      render: (_, { isEarnestMoneyDeduction, earnestMoneyDeductionAmount }) => {
        return (
          <Space>
            <div>{App.matchOption('yesOrNo', isEarnestMoneyDeduction)?.label}</div>
            <div>
              ，抵扣金额：
              <FormAmount.Format value={earnestMoneyDeductionAmount}></FormAmount.Format>
            </div>
          </Space>
        )
      },
    }),
  ]
}
