import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { DashboardArrowDown2 as ArrowDown2, DashboardArrowUp2 as ArrowUp2 } from '@/components/Dashboard/DashboardEntries'
import styles from './index.less'

// 字段块组件
// 历史平均(天)
// 12.4%⬇️
const Index = ({ title, value, pureValue }) => {
  const symbolIcon = useMemo(() => {
    if (pureValue > 0) return <ArrowUp2 />
    else if (pureValue < 0) return <ArrowDown2 />
  }, [pureValue])
  return (
    <div className={styles.block}>
      <div className={styles.block_title}>{title}</div>
      <div className={styles.block_value}>
        <span className={styles.block_count}>{value}</span>
        {symbolIcon}
      </div>
    </div>
  )
}

export default observer(Index)
