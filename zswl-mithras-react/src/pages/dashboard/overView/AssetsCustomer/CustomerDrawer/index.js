import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { LifeCycleCustomerList as CustomLifeCycle } from '@/components/LifeCycle/LifeCycleEntries'

const Index = ({ store }) => {
  return (
    <Drawer
      store={store.customerDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title="客户全周期"
      onClose={store.customerDrawer.close}
    >
      <CustomLifeCycle query={{ type: store.customerType }} />
    </Drawer>
  )
}

export default observer(Index)
