import Title from '../../OverviewTitle'
import AssetsPie from './AssetsPie/AssetsIndustryPie'
import CustomerPie from './CustomerPie/CustomerDepartmentPie'
import CountCard from './CountCard/CustomerLifecycleCountCards'
import CustomerDrawer from './CustomerDrawer/AssetsCustomerDrawer'
import styles from './index.less'
import { useMemo } from 'react'
import Store from './Store'

const DashboardOverviewAssetsCustomer = ({ title, dataDate }) => {
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

export default DashboardOverviewAssetsCustomer
