import { observer } from '@zswl/admin'
import { Title } from '@/components/Dashboard'
import { getUpdateDate } from '@/dashboard/DashboardUtils'
import { Tabs, Spin } from 'antd'
import { useEffect, useMemo } from 'react'
// import DeptIn from './DeptIn'
import DeptShip from './DeptShip'
import Store from './Store'
import styles from './index.less'

const Index = ({ title, iconType }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { activeKey, setActiveKey, updateTime } = store

  const items = [
    {
      label: `部门间排名`,
      key: 'deptShip',
      children: <DeptShip store={store} />,
    },
    // {
    //   label: `部门内排名`,
    //   key: 'deptIn',
    //   children: <DeptIn store={store} />,
    // },
  ]

  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <div className={styles.container}>
        <Tabs
          activeKey={activeKey}
          onChange={setActiveKey}
          className={styles.tabs}
          items={items}
          tabBarExtraContent={
            updateTime && <div className={styles.extra}>数据更新时间：{updateTime}</div>
          }
        />
      </div>
    </div>
  )
}

export default observer(Index)
