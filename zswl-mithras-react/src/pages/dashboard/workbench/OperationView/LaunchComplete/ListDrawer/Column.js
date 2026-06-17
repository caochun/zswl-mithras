import { orgSelect } from '@/utils/dashboardColumns'
import { AmountColumn } from '@/components/Format'

export const ALL_COLUMNS = [
  orgSelect({
    search: false,
  }),
  AmountColumn({
    title: '预算投放金额(万元)',
    dataIndex: 'payPlanAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '实际投放金额(万元)',
    dataIndex: 'payAmount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '差额(万元)',
    dataIndex: 'difference',
    initFormat: 1,
  }),
  AmountColumn({
    title: '达成率(%)',
    dataIndex: 'finishRate',
    initFormat: 1,
    // suffix: '%',
  }),
]
