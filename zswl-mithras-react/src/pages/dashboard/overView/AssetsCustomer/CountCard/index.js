import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { amountFormat } from '@/utils'
import { Spin, Skeleton } from 'antd'
import styles from './index.less'
import { canSeeDetailFn } from '@/utils/domains/dashboard/DashboardUtils'

// 不小于0
const getPlusMinusConfig = (num) => {
  if (num < 0) {
    return {
      isNotNegative: false,
      text: '减少',
      color: '#EB2222',
    }
  }
  return {
    isNotNegative: true,
    text: '新增',
    color: '#00B87A',
  }
}
const Index = ({ store }) => {
  const { customerLifeCycleData, getCustomerLifeCycleData, customerDrawer } = store

  useEffect(() => {
    getCustomerLifeCycleData()
  }, [])

  if (!customerLifeCycleData.length) {
    const arr = new Array(4).fill(1)
    return (
      <div className={styles.skeleton}>
        {arr.map((item, index) => {
          return <Skeleton paragraph={{ rows: 4 }} className={styles.item} key={index}></Skeleton>
        })}
      </div>
    )
  }

  return (
    <div className={styles.container}>
      {customerLifeCycleData?.map((item, index) => {
        return (
          <div
            key={index}
            className={styles.card}
            onClick={() => {
              if (canSeeDetailFn()) {
                customerDrawer.open({ type: item.type })
              }
            }}
          >
            <div className={styles.name}>{item.name}</div>
            <div className={styles.count}>
              <div className={styles.totalCount}>{amountFormat(item.totalCount)}</div>
              <div className={styles.newCountWrap}>
                <span className={styles.text}>
                  本月{getPlusMinusConfig(item.newCountThisMonth).text}
                </span>
                <span
                  className={styles.newCount}
                  style={{ color: getPlusMinusConfig(item.newCountThisMonth).color }}
                >
                  {item.newCountThisMonth}
                </span>
              </div>
            </div>
          </div>
        )
      })}
    </div>
  )
}

export default observer(Index)
