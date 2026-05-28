import { AmountFormat, MatchOptionColumn } from '@/components/Format'

const ALL_COLUMNS = [
  {
    title: '月份',
    dataIndex: 'yearAndMonth',
  },
  MatchOptionColumn({ title: '状态', dataIndex: 'yearAndMonth' }),
  AmountColumn({ title: '当期计提收入-实际利率法(含税)', width: 230, dataIndex: 'airCount' }),
  AmountColumn({
    title: '当期计提收入-实际利率法(不含税)',
    width: 230,
    dataIndex: 'airCountExcludeTax',
  }),
  AmountColumn({ title: '当期计提收入-剩余本金法(含税)', width: 230, dataIndex: 'rpCount' }),
  AmountColumn({
    title: '当期计提收入-剩余本金法(不含税)',
    width: 230,
    dataIndex: 'rpCountExcludeTax',
  }),
  AmountColumn({ title: '当期计提成本(含税)', dataIndex: 'costCount' }),
  AmountColumn({ title: '当期计提成本(不含税)', dataIndex: 'costCountExcludeTax' }),
  AmountColumn({ title: '当期计提印花税', dataIndex: 'stampDutyCount' }),
  {
    title: '确认日期',
    dataIndex: 'confirmDate',
  },
]
export default ALL_COLUMNS
