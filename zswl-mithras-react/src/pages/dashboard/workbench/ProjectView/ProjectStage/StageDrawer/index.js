import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { cloneElement } from 'react'
import { getNameColumns } from '../Config'
import { useMemo } from 'react'

const Index = ({ store, extraQueryParams }) => {
  const { curStageData } = store
  const { groupCode, group } = curStageData

  const drawerComponent = getNameColumns(groupCode)

  const tableComponent = useMemo(() => {
    return cloneElement(drawerComponent, { group, extraQueryParams })
  }, [drawerComponent, extraQueryParams, group])

  return (
    <Drawer
      store={store.stageDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={group}
      onClose={store.stageDrawer.close}
    >
      <div>{tableComponent}</div>
    </Drawer>
  )
}

export default observer(Index)
