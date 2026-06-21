import { observer } from '@zswl/admin'
import Title from '../../Title'
import { Tabs } from 'antd'
import { useMemo } from 'react'
import Department from '../../MyAchievement/Department/DashboardDepartmentAchievement'
import Store from './Store'
import styles from './index.less'

const WorkbenchMyAchievement = ({ title, iconType }) => {
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

export default observer(WorkbenchMyAchievement)
