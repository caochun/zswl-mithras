import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { getNameColumns } from '../Config'
import { useMemo, cloneElement } from 'react'

const Index = ({ store, extraQueryParams }) => {
  const { curInfoData } = store
  const { group, groupCode } = curInfoData
  const drawerComponent = getNameColumns(groupCode)

  const tableComponent = useMemo(() => {
    return cloneElement(drawerComponent, { group, extraQueryParams })
  }, [drawerComponent, group, extraQueryParams])

  return (
    <Drawer
      store={store.infoDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={group}
      onClose={store.infoDrawer.close}
    >
      <div>{tableComponent}</div>
    </Drawer>
  )
}

export default observer(Index)
