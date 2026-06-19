import { InputColumn, AmountColumn } from '@/components/Format'

const ALL_COLUMNS = [
  InputColumn({
    title: '部门',
    dataIndex: 'department',
    width: 160,
    search: true,
    fixed: 'left',
  }),
  InputColumn({
    title: '行业分类',
    dataIndex: 'industryType',
    width: 160,
    search: true,
  }),
  AmountColumn({
    title: '投放额',
    dataIndex: 'putAmount',
    width: 120,
    align: 'right',
  }),
  AmountColumn({
    title: '收入',
    dataIndex: 'income',
    width: 120,
    align: 'right',
  }),
  AmountColumn({
    title: '成本',
    dataIndex: 'cost',
    width: 120,
    align: 'right',
  }),
  AmountColumn({
    title: '差价',
    dataIndex: 'priceDiff',
    width: 120,
    align: 'right',
  }),
  AmountColumn({
    title: '税费+设备',
    dataIndex: 'taxAndEquipment',
    width: 120,
    align: 'right',
  }),
  AmountColumn({
    title: '利润总额',
    dataIndex: 'totalProfit',
    width: 120,
    align: 'right',
  }),
  AmountColumn({
    title: '收入',
    dataIndex: 'incomeAmount',
    width: 120,
    align: 'right',
  }),
  AmountColumn({
    title: '成本',
    dataIndex: 'costAmount',
    width: 120,
    align: 'right',
  }),
]

export default ALL_COLUMNS
