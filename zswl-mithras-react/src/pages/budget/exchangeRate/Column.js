import { AmountColumn, DateColumn, MatchOptionColumn, NumberColumn } from '@/components/Format'

/**
 * 汇率设置页面表格列配置
 * 定义汇率管理表格的所有列信息
 */
const ALL_COLUMNS = [
  {
    title: '年份',
    dataIndex: 'targetYear',
    search: false,
    width: 100,
  },
  {
    title: '月份',
    dataIndex: 'targetMonth',
    search: false,
    width: 100,
  },
  MatchOptionColumn({
    title: '币种',
    dataIndex: 'currency',
    search: false,
    matchOption: 'currencyType',
    width: 120,
  }),
  AmountColumn({
    title: '汇率',
    dataIndex: 'exchangeRate',
    precision: 4,
    initFormat: 1,
    width: 150,
  }),
  {
    title: '汇率日期',
    dataIndex: 'targetDate',
    width: 120,
  },
  DateColumn({
    title: '更新时间',
    dataIndex: 'updateTime',
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
    width: 180,
  }),
]

export default ALL_COLUMNS
