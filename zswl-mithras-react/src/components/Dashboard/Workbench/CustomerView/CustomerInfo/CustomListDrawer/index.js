import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { useMemo, cloneElement } from 'react'
import { getNameColumns } from '../Config'

const Index = ({ store }) => {
  const { curCardData } = store
  const { group, groupCode } = curCardData
  const drawerComponent = getNameColumns(groupCode)

  const tableComponent = useMemo(() => {
    return cloneElement(drawerComponent, { group })
  }, [drawerComponent])

  return (
    <Drawer
      store={store.customerListDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={group}
      onClose={store.customerListDrawer.close}
    >
      <div>{tableComponent}</div>
    </Drawer>
  )
}

export default observer(Index)
