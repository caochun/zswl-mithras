import Title from '../../OverviewTitle'
import { observer } from '@zswl/admin'
import { AssetsBalance, AssetsLoan } from './OverviewPanel/AssetsOverviewPanels'
import ChinaMap from './ChinaMap/AssetsProvinceMap'
import Ranking from './Ranking/AssetsViewRanking'
import Store from './Store'
import styles from './index.less'
import { useMemo } from 'react'

const DashboardOverviewAssetsView = ({ title, dataDate }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  return (
    <div>
      <Title
        title={title}
        extra={dataDate && <div className={styles.extra}>数据截止时间：{dataDate}</div>}
      ></Title>
      <div className={styles.container}>
        <div className={styles.item}>
          <AssetsBalance store={store}></AssetsBalance>
          <ChinaMap store={store}></ChinaMap>
        </div>
        <div className={styles.item}>
          <AssetsLoan store={store} />
          <Ranking store={store} />
        </div>
      </div>
    </div>
  )
}

export default observer(DashboardOverviewAssetsView)
