import { observer } from '@zswl/admin'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import { useMemo } from 'react'
import styles from '../index.less'

const AfterLeaseCheckPlanDetailBaseInfo = ({ detail, planTypeText }) => {
  const { checkEndDate, checkStartDate, finishCount, totalCount, planStatus } = detail
  const baseInfoList = useMemo(() => {
    return [
      {
        img: '/public/assets/image/icon_time.png',
        title: planTypeText,
        desc: '检查所属时间',
      },
      {
        img: '/public/assets/image/icon_finish.png',
        title: `${finishCount}/${totalCount}`,
        desc: '检查完成/需检客户',
      },
      {
        img: '/public/assets/image/icon_time.png',
        title: `${checkStartDate}~${checkEndDate}`,
        desc: '检查时间',
      },
      {
        img: '/public/assets/image/icon_plan.png',
        title: getKeyOptionsLabelMapPlus('afterLeaseCheckPlanStatusEnum')[planStatus],
        desc: '计划状态',
      },
    ]
  }, [checkEndDate, checkStartDate, finishCount, totalCount, planStatus, planTypeText])

  return (
    <div className={styles.baseInfo}>
      {baseInfoList.map((item) => {
        return (
          <div className={styles.item} key={item.desc}>
            <div className={styles.left}>
              <img src={item.img} />
            </div>
            <div className={styles.right}>
              <div className={styles.title}>{item.title}</div>
              <div className={styles.desc}>{item.desc}</div>
            </div>
          </div>
        )
      })}
    </div>
  )
}

export default observer(AfterLeaseCheckPlanDetailBaseInfo)
