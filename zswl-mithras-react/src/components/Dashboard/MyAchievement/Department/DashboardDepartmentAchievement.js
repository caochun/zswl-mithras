import { Table } from '@zswl/components'
import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import AchievementCard from '../AchievementCard/AchievementSummaryCards'
import { columns } from '../../AchievementColumns'
import Store from './Store'
import { saveServer } from '@/utils'

const Index = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { deptData } = store
  const { cardList } = deptData ?? {}

  return (
    <div>
      <AchievementCard cardData={cardList ?? []} />
      <Table
        columnsFilter={'MyAchievement_Department_1'}
        onFilter={(key, val) => saveServer('MyAchievement_Department_1', val)}
        scroll={{ x: true }}
        style={{ marginTop: 20 }}
        bordered
        store={store.deptTableStore}
        columns={columns}
      ></Table>
    </div>
  )
}

export default observer(Index)
