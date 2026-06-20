import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import RankingTable from '../RankingTable'

const Index = ({ store }) => {
  return (
    <Drawer
      store={store.allRankingDrawer}
      width={800}
      destroyOnClose
      extra={null}
      title="区域排名明细"
      onClose={store.allRankingDrawer.close}
    >
      <RankingTable
        store={store.allRankingTable}
        type={'province'}
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: store.downLoadRank,
          },
        ]}
      />
    </Drawer>
  )
}

export default observer(Index)
