import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'
import { DashboardAchievementColumns as columns } from '@/components/Dashboard/DashboardEntries'

export { columns }

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
