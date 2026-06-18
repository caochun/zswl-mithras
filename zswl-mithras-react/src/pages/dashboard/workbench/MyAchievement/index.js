import { observer } from '@zswl/admin'
import { DashboardTitle as Title } from '@/components/Dashboard/DashboardEntries'
import { getUpdateDate } from '@/utils/domains/dashboard/DashboardUtils'
import { Tabs, Spin } from 'antd'
import { useEffect, useMemo } from 'react'
import { MyAchievementDepartment as Department } from '@/components/Dashboard/MyAchievementEntries'
// import Person from './Person'
import Store from './Store'
import styles from './index.less'

const Index = ({ title, iconType }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { activeKey, setActiveKey } = store

  const items = [
    {
      label: `部门业绩`,
      key: 'dept',
      children: <Department />,
    },
    // {
    //   label: `个人业绩`,
    //   key: 'person',
    //   children: <Person />,
    // },
  ]

  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <div className={styles.container}>
        <Tabs activeKey={activeKey} onChange={setActiveKey} className={styles.tabs} items={items} />
      </div>
    </div>
  )
}

export default observer(Index)
