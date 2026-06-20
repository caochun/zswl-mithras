import { Table, TableStore } from '@zswl/components'
import { AmountColumn } from '@/components/Format'
import AchievementCard from '../../../MyAchievement/AchievementCard'
import { saveServer } from '@/utils'

const cardData = [
  {
    title: '投放',
    total: {
      value: 23334,
      unit: '万元',
    },
    global: {
      value: 234,
      unit: '万元',
    },
    completeRate: {
      value: 24,
      unit: '%',
    },
  },
  {
    title: '利润',
    total: {
      value: 234,
      unit: '万元',
    },
    global: {
      value: 234,
      unit: '万元',
    },
    completeRate: {
      value: 24,
      unit: '%',
    },
  },
]

const Index = () => {
  const table = new TableStore({
    request: async (params) => {
      return [
        {
          项目经理: '王喜',
          投放达成率: 23,
          投放排名: 23,
          利润达成率: 23,
          利润排名: 23,
        },
        {
          项目经理: '王喜',
          投放达成率: 23,
          投放排名: 23,
          利润达成率: 23,
          利润排名: 23,
        },
      ]
    },
  })
  return (
    <div>
      <AchievementCard cardData={cardData} />
      <Table
              columnsFilter={'MyAchievement_Person_1'}
              onFilter={(key,val) => saveServer('MyAchievement_Person_1',val)}
        style={{ marginTop: 20 }}
        bordered
        store={table}
        columns={[
          {
            title: ' ',
            children: [
              {
                title: '项目经理',
                dataIndex: '项目经理',
              },
            ],
          },
          {
            title: '投放',
            children: [
              AmountColumn({
                title: '投放达成率',
                dataIndex: '投放达成率',
                initFormat: 1,
                suffix: '%',
              }),
              AmountColumn({
                title: '投放排名',
                dataIndex: '投放排名',
                initFormat: 1,
              }),
            ],
          },
          {
            title: '利润',
            children: [
              AmountColumn({
                title: '利润达成率',
                dataIndex: '利润达成率',
                suffix: '%',
                initFormat: 1,
              }),
              AmountColumn({
                title: '利润排名',
                dataIndex: '利润排名',
                initFormat: 1,
              }),
            ],
          },
        ]}
      ></Table>
    </div>
  )
}

export default Index
