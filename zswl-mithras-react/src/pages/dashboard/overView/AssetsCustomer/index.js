import { DashboardOverviewTitle as Title } from '@/components/Dashboard/DashboardEntries'
import AssetsPie from './AssetsPie'
import CustomerPie from './CustomerPie'
import CountCard from './CountCard'
import CustomerDrawer from './CustomerDrawer'
import styles from './index.less'
import { useMemo } from 'react'
import Store from './Store'

const Index = ({ title, dataDate }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  return (
    <div>
      <Title
        title={title}
        extra={dataDate && <div className={styles.extra}>数据截止时间：{dataDate}</div>}
      ></Title>
      <div className={styles.content}>
        <AssetsPie store={store}></AssetsPie>
        <CustomerPie store={store}></CustomerPie>
        <CountCard store={store}></CountCard>
        <CustomerDrawer store={store}></CustomerDrawer>
      </div>
    </div>
  )
}

export default Index
