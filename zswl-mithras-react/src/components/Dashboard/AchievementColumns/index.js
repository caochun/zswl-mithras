import { AmountColumn } from '@/components/Format'

export const columns = [
  {
    title: '部门',
    dataIndex: 'deptName',
    fixed: 'left',
  },
  {
    title: '投放',
    children: [
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'advertisingAmount',
        initFormat: 1,
      }),
      AmountColumn({
        title: '目标(万元)',
        dataIndex: 'advertisingTarget',
        initFormat: 1,
      }),
      AmountColumn({
        title: '达成率',
        dataIndex: 'deliveryAchievementRate',
        initFormat: 1,
        suffix: '%',
      }),
      AmountColumn({
        title: '排名',
        dataIndex: 'deliveryAchievementRateSort',
        initFormat: 1,
      }),
    ],
  },
  {
    title: '收入',
    children: [
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'incomeAmount',
        initFormat: 1,
      }),
      AmountColumn({
        title: '目标(万元)',
        dataIndex: 'revenueTarget',
        initFormat: 1,
      }),
      AmountColumn({
        title: '达成率',
        dataIndex: 'revenueAchievementRate',
        suffix: '%',
        initFormat: 1,
      }),
      AmountColumn({
        title: '排名',
        dataIndex: 'revenueAchievementRateSort',
        initFormat: 1,
      }),
    ],
  },
  {
    title: '利润',
    children: [
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'profitAmount',
        initFormat: 1,
      }),
      AmountColumn({
        title: '目标(万元)',
        dataIndex: 'profitTarget',
        initFormat: 1,
      }),
      AmountColumn({
        title: '达成率',
        dataIndex: 'profitAchievementRate',
        suffix: '%',
        initFormat: 1,
      }),
      AmountColumn({
        title: '排名',
        dataIndex: 'profitAchievementRateSort',
        initFormat: 1,
      }),
    ],
  },
]
