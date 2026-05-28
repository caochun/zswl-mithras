import { Table } from '@zswl/components'
import { AmountColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

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
        // sorter: {
        //   compare: (a, b) => a.deliveryAchievementRateSort - b.deliveryAchievementRateSort,
        // },
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
        // sorter: {
        //   compare: (a, b) => a.revenueAchievementRateSort - b.revenueAchievementRateSort,
        // },
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
        // sorter: {
        //   compare: (a, b) => a.profitAchievementRateSort - b.profitAchievementRateSort,
        // },
      }),
    ],
  },
]

const Index = ({ store }) => {
  return (
    <div>
      <Table
              columnsFilter={'AchievementRank_DeptShip_1'}
              onFilter={(key,val) => saveServer('AchievementRank_DeptShip_1',val)}
        scroll={{ x: true }}
        bordered
        store={store.deptShipTableStore}
        columns={columns}
      ></Table>
    </div>
  )
}

export default observer(Index)
