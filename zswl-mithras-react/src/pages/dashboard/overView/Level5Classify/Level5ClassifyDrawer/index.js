import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { AfterLeaseLevel5Classify as Level5Classify } from '@/components/AfterLease/RentCollectionEntries'

const Index = ({ store }) => {
  return (
    <Drawer
      store={store.level5ClassifyDrawerDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title="五级分类明细"
      onClose={store.level5ClassifyDrawerDrawer.close}
    >
      <div>
        <Level5Classify query={{ form: 'dashboard' }} />
      </div>
    </Drawer>
  )
}

export default observer(Index)
