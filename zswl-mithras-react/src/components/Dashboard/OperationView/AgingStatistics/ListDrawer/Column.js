import { InputColumn, AmountColumn } from '@/components/Format'
import { orgSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'

export const ALL_COLUMNS = [
  orgSelect({
    search: false,
  }),
  AmountColumn({
    title: '立项均耗',
    dataIndex: 'projEstaTotalTime',
    initFormat: 1,
    align: 'center',
  }),
  AmountColumn({
    title: '尽调-出具尽调报告均耗',
    dataIndex: 'dueDiligenceTotalTime',
    initFormat: 1,
    align: 'center',
  }),
  {
    title: '评审均耗',
    children: [
      AmountColumn({
        title: '评审均耗',
        dataIndex: 'reviewTotalTime',
        initFormat: 1,
        align: 'center',
      }),
      AmountColumn({
        title: '纪要均耗',
        dataIndex: 'summaryTotalTime',
        initFormat: 1,
        align: 'center',
      }),
    ],
  },

  AmountColumn({
    title: '立项-投放均耗',
    dataIndex: 'projEstaPaidInTotalTime',
    initFormat: 1,
    align: 'center',
  }),
  AmountColumn({
    title: '评审-投放均耗',
    dataIndex: 'reviewPaidInTotalTime',
    initFormat: 1,
    align: 'center',
  }),
]
