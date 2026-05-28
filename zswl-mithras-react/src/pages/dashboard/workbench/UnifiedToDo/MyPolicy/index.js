import { Tabs } from 'antd'
import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import Overdue from './Overdue'
import Store from './Store'
import styles from './index.less'
import CreateDraw from './CreateDraw'

const Index = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const { activityKey, setActivityKey } = store
  const items = [
    {
      label: `即将到期保单`,
      key: 'NOT_OVERDUE',
      children: <Overdue store={store} />,
    },
    {
      label: `逾期保单`,
      key: 'OVERDUE',
      children: <Overdue store={store} />,
    },
  ]

  return (
    <div>
      <div className={styles.container}>
        <Tabs
          destroyInactiveTabPane
          className={styles.tabs}
          items={items}
          onChange={setActivityKey}
          activeKey={activityKey}
          tabBarExtraContent={
            <Button
              type="primary"
              onClick={store.batchDown}
              disabled={!store.table.getList().length}
            >
              批量下载
            </Button>
          }
        />
      </div>
      <CreateDraw store={store}></CreateDraw>
    </div>
  )
}

export default observer(Index)
