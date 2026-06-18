import { OverviewTitle as Title } from '@/components/Dashboard'
import { observer } from '@zswl/admin'
import { AssetsBalance, AssetsLoan } from './OverviewPanel'
import ChinaMap from './ChinaMap'
import Ranking from './Ranking'
import Store from './Store'
import styles from './index.less'
import { useMemo } from 'react'

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

export default observer(Index)
