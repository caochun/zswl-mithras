import { AmountColumn, InputColumn, MatchOptionColumn, AmountFormat } from '@/components/Format'

const ALL_COLUMNS = [
  {
    title: '融资编号',
    dataIndex: 'financingCode',
    width: 160,
  },
  {
    title: '融资渠道',
    dataIndex: 'organizationName',
  },
  InputColumn({
    title: '起息日',
    dataIndex: 'valueDate',
  }),
  InputColumn({
    title: '业务类型',
    dataIndex: 'bizTypeDisplay',
  }),
  MatchOptionColumn({
    title: '借款性质',
    dataIndex: 'loanProperty',
    matchOption: 'fundFinancingTimeLimitTypeEnum',
  }),
  AmountColumn({ title: '利率', dataIndex: 'financingRate', suffix: '%' }),
  AmountColumn({ title: '当年累积应付利息(元)', dataIndex: 'yearCapitalCost' }),
  AmountColumn({ title: '当年累积应付利息(税后)(元)', dataIndex: 'yearCapitalCostAfterTax' }),
  AmountColumn({ title: '当期应付利息(元)', dataIndex: 'termCapitalCost' }),
  AmountColumn({ title: '当期应付利息(税后)(元)', dataIndex: 'termCapitalCostAfterTax' }),
  {
    title: '会计期间',
    dataIndex: 'calculateTime',
  },
  {
    title: '更新日期',
    dataIndex: 'updateTime',
  },

  // 详情数据
  AmountColumn({ title: '融资金额(元)', dataIndex: 'financingAmount' }),

  {
    title: '日期',
    dataIndex: 'interestDate',
  },
  AmountColumn({ title: '融资余额(元)', dataIndex: 'remainingAmount' }),
  AmountColumn({ title: '还款本金(元)', dataIndex: 'principleAmount' }),
  AmountColumn({ title: '还款利息(元)', dataIndex: 'interestAmount' }),
  AmountColumn({ title: '融资利率', dataIndex: 'financingRate', suffix: '%' }),
  AmountColumn({
    title: '日利率',
    dataIndex: 'dailyRate',
    precision: 4,
    suffix: '%',
    needSmallNumber: false,
  }),
  AmountColumn({ title: '当日应付利息(元)', dataIndex: 'dailyAmount' }),
  AmountColumn({ title: '当年累计应付利息(元)', dataIndex: 'yearCapitalCost' }),
  // AmountColumn({ title: '当日累计应付利息(税后)(元)', dataIndex: 'yearCapitalCostAfterTax' }),
  MatchOptionColumn({
    title: '是否已确认',
    dataIndex: 'isConfirmed',
    matchOption: 'yesOrNo',
    editable: false,
  }),
  AmountColumn({
    title: '钆差金额',
    dataIndex: 'financingCostDiff',
    editable: true,
    wrapItemProps: {
      inputConfig: {
        min: -Infinity,
      },
    },
  }),
  AmountColumn({
    title: '期初计提利息余额',
    dataIndex: 'beginOfPeriodInterestBalance',
    wrapItemProps: {
      inputConfig: {
        min: -Infinity,
      },
    },
  }),
  AmountColumn({
    title: '期末计提利息余额',
    dataIndex: 'endOfPeriodInterestBalance',
    editable: true,
    wrapItemProps: {
      inputConfig: {
        min: -Infinity,
      },
    },
  }),
]
export default ALL_COLUMNS
