import { AmountEditable, InputColumn, AmountColumn, TextAreaEditable } from '@/components/Format'
import { formatPercent, amountFormat } from '@/utils'
import { Form } from '@zswl/components'
import mathjs from '@/utils/math'
import YearRate from '../FundYearRate'

export const SETTLE_COLUMNS = [
  InputColumn({
    title: '融资机构',
    dataIndex: 'organizationName',
  }),
  AmountColumn({
    title: '融资金额(元)',
    dataIndex: 'financingAmount',
  }),
  AmountColumn({
    title: '剩余未还本金(元)',
    dataIndex: 'lastPrincipal',
  }),
  InputColumn({
    title: '融资期限',
    dataIndex: 'financingTerm',
  }),
  {
    title: '提前偿还金额(元)',
    dataIndex: 'earlyRepayAmount',
    requiredMark: true,
    editable: (val) => AmountEditable(val, 'earlyRepayAmount', { required: true, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '提前偿还本金(元)',
    dataIndex: 'earlyPrincipleAmount',
    requiredMark: true,
    editable: (val) =>
      AmountEditable(val, 'earlyPrincipleAmount', { required: true, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '提前偿还利息(元)',
    dataIndex: 'earlyInterestAmount',
    editable: (val) =>
      AmountEditable(val, 'earlyInterestAmount', { required: false, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '违约金(元)',
    dataIndex: 'liquidatedDamagesAmount',
    editable: (val) =>
      AmountEditable(val, 'liquidatedDamagesAmount', { required: false, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '其它费用(元)',
    dataIndex: 'otherFeeAmount',
    editable: (val) => AmountEditable(val, 'otherFeeAmount', { required: false, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '违约金减免(元)',
    dataIndex: 'remissionAmount',
    editable: (val) => AmountEditable(val, 'remissionAmount', { required: false, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '合计金额(元)',
    dataIndex: 'totalCreditLimit',
    span: 2,
    editable: ({}) => {
      return (
        <Form.Item
          dependencies={[
            'earlyRepayAmount',
            'liquidatedDamagesAmount',
            'remissionAmount',
            'otherFeeAmount',
          ]}
          noStyle
        >
          {({ getFieldValue }) => {
            const earlyRepayAmount = getFieldValue('earlyRepayAmount') ?? 0
            const liquidatedDamagesAmount = getFieldValue('liquidatedDamagesAmount') ?? 0
            const remissionAmount = getFieldValue('remissionAmount') ?? 0
            const otherFeeAmount = getFieldValue('otherFeeAmount') ?? 0
            const val = mathjs.toNonExponential(
              mathjs.format(
                mathjs
                  .chain(earlyRepayAmount)
                  .add(liquidatedDamagesAmount)
                  .subtract(remissionAmount)
                  .add(otherFeeAmount)
                  .done()
              )
            )
            return <div>{val}</div>
          }}
        </Form.Item>
      )
    },
    render: (val, record) => {
      const {
        earlyRepayAmount = 0,
        liquidatedDamagesAmount = 0,
        remissionAmount = 0,
        otherFeeAmount = 0,
      } = record
      const result = mathjs.toNonExponential(
        mathjs.format(
          mathjs
            .chain(earlyRepayAmount / 10000)
            .add(liquidatedDamagesAmount / 10000)
            .subtract(remissionAmount / 10000)
            .add(otherFeeAmount / 10000)
            .done()
        )
      )
      return result
    },
  },
  {
    title: '提前还款原因',
    dataIndex: 'reason',
    requiredMark: true,
    span: 2,
    editable: TextAreaEditable({ required: true }),
  },
]

const OLD_YEARRETE = {
  interestRateType: { name: 'old_interestRateType', disabled: true },
  lprType: { name: 'old_lprType', disabled: true },
  lprRatePercent: { name: 'old_lprRatePercent', disabled: true },
  lprAddPercent: { name: 'old_lprAddPercent', disabled: true },
}
const NEW_YEARRETE = {
  interestRateType: { name: 'interestRateType', disabled: false },
  lprType: { name: 'lprType', disabled: false },
  lprRatePercent: { name: 'lprRatePercent', disabled: false },
  lprAddPercent: { name: 'lprAddPercent', disabled: true },
}

export const LPR_COLUMNS = [
  {
    title: '原借款年利率',
    dataIndex: 'oldYearRate',
    span: 2,
    requiredMark: true,
    editable: (val, record) => {
      return <YearRate data={val} config={OLD_YEARRETE} />
    },
    render: (val, record) => <YearRate.Detail data={record} config={OLD_YEARRETE} />,
  },
  {
    title: '现借款年利率',
    dataIndex: 'yearRate',
    span: 2,
    requiredMark: true,
    editable: (val) => {
      return <YearRate data={val} config={NEW_YEARRETE} />
    },
    render: (val, record) => <YearRate.Detail data={record} config={NEW_YEARRETE} />,
  },
]

export const CHANGE_TITLE = {
  CHANGE_LPR: {
    title: 'LPR调整',
    subTitle: '借款利率调整方案',
  },
  CHANGE_EARLY_SETTLE: {
    title: '提前还款',
    subTitle: '提前还款方案',
  },
}
