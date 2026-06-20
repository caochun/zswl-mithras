import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { useMemo, cloneElement } from 'react'
import { getItemConfigByGroupCode } from '../Config'

const Index = ({ store, extraQueryParams }) => {
  const { curCardData } = store
  const { group, groupCode } = curCardData
  const drawerComponent = getItemConfigByGroupCode(groupCode)?.component ?? <div></div>

  const tableComponent = useMemo(() => {
    return cloneElement(drawerComponent, { groupCode, extraQueryParams })
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
