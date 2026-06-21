import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { cloneElement, useMemo } from 'react'

const AfterLeaseListDrawer = ({ store, getNameColumns }) => {
  const { curCardData } = store
  const { group, groupCode } = curCardData
  const drawerComponent = getNameColumns(groupCode)

  const tableComponent = useMemo(() => {
    return cloneElement(drawerComponent, { group })
  }, [drawerComponent, group])

  return (
    <Drawer
      store={store.listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={group}
      onClose={store.listDrawer.close}
    >
      <div>{tableComponent}</div>
    </Drawer>
  )
}

export default observer(AfterLeaseListDrawer)
