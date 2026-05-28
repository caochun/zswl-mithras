import { orgSelect } from '@/pages/dashboard/workbench/Column'
import { AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  orgSelect({
    title: '业务部门',
    search: false,
  }),
  AmountColumn({
    title: '总个数',
    dataIndex: 'projSum',
    initFormat: 1,
  }),
  AmountColumn({
    title: '调整人数(人)',
    dataIndex: 'adjustPersonSum',
    initFormat: 1,
  }),
  AmountColumn({
    title: '总金额(万元)',
    dataIndex: 'amountSum',
    initFormat: 1,
  }),
  AmountColumn({
    title: '人均个数',
    dataIndex: 'personAverageProjSum',
    initFormat: 1,
  }),
  AmountColumn({
    title: '人均金额(万元)',
    dataIndex: 'personAverageAmountSum',
    initFormat: 1,
  }),
  AmountColumn({
    title: '件均金额(万元)',
    dataIndex: 'pieceAverageAmountSum',
    initFormat: 1,
  }),
]
