import { getQuery, history, observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { Spin, Tabs } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import Financial from './Financial'
import financialStore from './Financial/store'
import Project from './Project'
import projectStore from './Project/store'

const DEFAULT_TAB = 'FINANCIAL'

const Index = ({ pathname }) => {
  const currentTab = getQuery('tab') || DEFAULT_TAB
  const [isCheckingPermission, setIsCheckingPermission] = useState(true)

  useEffect(() => {
    const checkPermissions = async () => {
      setIsCheckingPermission(true)

      await Promise.allSettled([
        financialStore.table.search().catch(() => {}),
        projectStore.table.search().catch(() => {}),
      ])

      setIsCheckingPermission(false)
    }

    checkPermissions()
  }, [])

  const tabItems = useMemo(() => {
    const items = []

    if (financialStore.hasPermission === true) {
      items.push({
        label: '资金端资料',
        key: 'FINANCIAL',
        children: <Financial pathname={pathname} />,
      })
    }

    if (projectStore.hasPermission === true) {
      items.push({
        label: '项目端资料',
        key: 'PROJECT',
        children: <Project pathname={pathname} />,
      })
    }

    return items
  }, [financialStore.hasPermission, projectStore.hasPermission, pathname])

  const firstAvailableTab = tabItems.length > 0 ? tabItems[0].key : null

  useEffect(() => {
    if (!firstAvailableTab) return

    const currentTabExists = tabItems.some((item) => item.key === currentTab)

    if (!getQuery('tab') || !currentTabExists) {
      history.replace({
        pathname,
        search: `?tab=${firstAvailableTab}`,
      })
    }
  }, [firstAvailableTab, currentTab, tabItems, pathname])

  const handleTabChange = (key) => {
    history.replace({
      pathname,
      search: `?tab=${key}`,
    })
  }

  if (isCheckingPermission) {
    return (
      <Page>
        <div style={{ padding: '50px', textAlign: 'center' }}>
          <Spin size="large" tip="正在加载..." />
        </div>
      </Page>
    )
  }

  if (tabItems.length === 0) {
    return (
      <Page>
        <div style={{ padding: '20px', textAlign: 'center' }}>暂无可访问的标签页</div>
      </Page>
    )
  }

  return (
    <Page>
      <Tabs activeKey={currentTab} onChange={handleTabChange} items={tabItems}></Tabs>
    </Page>
  )
}

export default observer(Index)
