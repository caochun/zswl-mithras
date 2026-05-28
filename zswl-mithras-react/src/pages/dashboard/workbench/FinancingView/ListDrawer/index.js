import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'
import { useMemo, cloneElement } from 'react'
import { getItemConfigByGroupCode } from '../Config'

const Index = ({ store }) => {
  const { curCardData } = store
  console.log('curCardData: ', curCardData)
  const { group, groupCode } = curCardData

  const drawerComponent = getItemConfigByGroupCode(groupCode)?.component ?? <div></div>

  const tableComponent = useMemo(() => {
    return cloneElement(drawerComponent, { groupCode, curCardData })
  }, [curCardData, drawerComponent])

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

export default observer(Index)
