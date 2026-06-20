import { Table } from '@zswl/components'
import { AmountColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  return (
    <div>
      <Table
              columnsFilter={'AchievementRank_DeptIn_1'}
              onFilter={(key,val) => saveServer('AchievementRank_DeptIn_1',val)}
        bordered
        store={store.deptInTableStore}
        columns={[
          {
            title: ' ',
            children: [
              {
                title: '部门',
                dataIndex: 'deptName',
              },
            ],
          },
          {
            title: '投放',
            children: [
              AmountColumn({
                title: '投放金额(万元)',
                dataIndex: 'advertisingAmount',
                initFormat: 1,
              }),
              AmountColumn({
                title: '投放达成率',
                dataIndex: 'deliveryAchievementRate',
                initFormat: 1,
                suffix: '%',
              }),
              AmountColumn({
                title: '投放达成率排名',
                dataIndex: 'deliveryAchievementRateSort',
                initFormat: 1,
                sorter: {
                  compare: (a, b) => a.deliveryAchievementRateSort - b.deliveryAchievementRateSort,
                  multiple: 1,
                },
              }),
            ],
          },
          {
            title: '收入',
            children: [
              AmountColumn({
                title: '收入达成率',
                dataIndex: 'revenueAchievementRate',
                suffix: '%',
                initFormat: 1,
              }),
              AmountColumn({
                title: '收入达成率排名',
                dataIndex: 'revenueAchievementRateSort',
                initFormat: 1,
                sorter: {
                  compare: (a, b) => a.revenueAchievementRateSort - b.revenueAchievementRateSort,
                  multiple: 2,
                },
              }),
            ],
          },
          {
            title: '利润',
            children: [
              AmountColumn({
                title: '利润达成率',
                dataIndex: 'profitAchievementRate',
                suffix: '%',
                initFormat: 1,
              }),
              AmountColumn({
                title: '利润达成率排名',
                dataIndex: 'profitAchievementRateSort',
                initFormat: 1,
                sorter: {
                  compare: (a, b) => a.profitAchievementRateSort - b.profitAchievementRateSort,
                  multiple: 3,
                },
              }),
            ],
          },
        ]}
      ></Table>
    </div>
  )
}

export default observer(Index)
