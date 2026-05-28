import { AmountColumn, InputColumn, MatchOptionColumn } from '@/components/Format'

const ALL_COLUMNS = () => [
  InputColumn({
    title: '借据编号',
    dataIndex: 'receiptCode',
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 250,
  }),
  InputColumn({
    title: '日期',
    dataIndex: 'incomeDate',
  }),
  AmountColumn({
    title: '长期应收款期初余额',
    dataIndex: 'beginOfTermBalance',
  }),
  AmountColumn({
    title: '当天应收租金',
    dataIndex: 'rent',
  }),
  AmountColumn({
    title: '当天确认收入',
    dataIndex: 'income',
  }),
  AmountColumn({
    title: '不含税收入',
    dataIndex: 'incomeWithoutTax',
  }),
  AmountColumn({
    title: '税额',
    dataIndex: 'tax',
  }),
  AmountColumn({
    title: '长期应收余额',
    dataIndex: 'endOfTermBalance',
  }),
  InputColumn({
    title: '日折现率',
    dataIndex: 'dailyDiscountRate',
  }),
  MatchOptionColumn({
    title: '收入是否确认',
    dataIndex: 'isConfirmed',
    matchOption: 'yesOrNo',
  }),
]
export default ALL_COLUMNS
