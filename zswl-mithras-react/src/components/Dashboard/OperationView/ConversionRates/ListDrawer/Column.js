import { InputColumn, AmountColumn } from '@/components/Format'
import { orgSelect } from '@/utils/domains/dashboard/DashboardUtilsColumns'

export const ALL_COLUMNS = [
  orgSelect({
    search: false,
  }),
  AmountColumn({
    title: '调整人数(人)',
    dataIndex: 'adjustCount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '拜访人数(人)',
    dataIndex: 'visitCount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '立项个数(个)',
    dataIndex: 'projEstaCount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '尽调个数(个)',
    dataIndex: 'dueDiligenceCount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '评审个数(个)',
    dataIndex: 'reviewCount',
    initFormat: 1,
  }),
  AmountColumn({
    title: '投放个数(个)',
    dataIndex: 'deliveryCount',
    initFormat: 1,
  }),
  {
    title: '「访客-立项」转化率',
    children: [
      AmountColumn({
        title: '当期',
        dataIndex: 'projEstaVisitCurrentTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年同期',
        dataIndex: 'projEstaVisitLastTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年平均',
        dataIndex: 'projEstaVisitLastAverage',
        initFormat: 1,
      }),
      AmountColumn({
        title: '当年平均',
        dataIndex: 'projEstaVisitCurrentAverage',
        initFormat: 1,
      }),
    ],
  },
  {
    title: '「访客-投放」转化率',
    children: [
      AmountColumn({
        title: '当期',
        dataIndex: 'deliveryVisitCurrentTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年同期',
        dataIndex: 'deliveryVisitLastTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年平均',
        dataIndex: 'deliveryVisitLastAverage',
        initFormat: 1,
      }),
      AmountColumn({
        title: '当年平均',
        dataIndex: 'deliveryVisitCurrentAverage',
        initFormat: 1,
      }),
    ],
  },
  {
    title: '「立项-尽调」转化率',
    children: [
      AmountColumn({
        title: '当期',
        dataIndex: 'dueDiliProjEstaCurrentTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年同期',
        dataIndex: 'dueDiliProjEstaLastTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年平均',
        dataIndex: 'dueDiliProjEstaLastAverage',
        initFormat: 1,
      }),
      AmountColumn({
        title: '当年平均',
        dataIndex: 'dueDiliProjEstaCurrentAverage',
        initFormat: 1,
      }),
    ],
  },
  {
    title: '「尽调-评审」转化率',
    children: [
      AmountColumn({
        title: '当期',
        dataIndex: 'reviewDueDiliCurrentTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年同期',
        dataIndex: 'reviewDueDiliLastTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年平均',
        dataIndex: 'reviewDueDiliLastAverage',
        initFormat: 1,
      }),
      AmountColumn({
        title: '当年平均',
        dataIndex: 'reviewDueDiliCurrentAverage',
        initFormat: 1,
      }),
    ],
  },
  {
    title: '「尽调-投放」转化率',
    children: [
      AmountColumn({
        title: '当期',
        dataIndex: 'deliveryDueDiliCurrentTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年同期',
        dataIndex: 'deliveryDueDiliLastTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年平均',
        dataIndex: 'deliveryDueDiliLastAverage',
        initFormat: 1,
      }),
      AmountColumn({
        title: '当年平均',
        dataIndex: 'deliveryDueDiliCurrentAverage',
        initFormat: 1,
      }),
    ],
  },
  {
    title: '「评审-投放」转化率',
    children: [
      AmountColumn({
        title: '当期',
        dataIndex: 'deliveryReviewCurrentTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年同期',
        dataIndex: 'deliveryReviewLastTerm',
        initFormat: 1,
      }),
      AmountColumn({
        title: '去年平均',
        dataIndex: 'deliveryReviewLastAverage',
        initFormat: 1,
      }),
      AmountColumn({
        title: '当年平均',
        dataIndex: 'deliveryReviewCurrentAverage',
        initFormat: 1,
      }),
    ],
  },
]
