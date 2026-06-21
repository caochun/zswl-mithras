import { observer } from '@zswl/admin'
import Clients from '/public/assets/risk/monitoringAlertList/clients.svg'
import IconRedLightwarning from '/public/assets/risk/monitoringAlertList/NumbeEarlyWarningCustomers.svg'
import IconYellowLightWarning from '/public/assets/risk/monitoringAlertList/PublicOpinionOccursCustomers.svg'
import styles from './index.less'

const StatsItem = ({ icon, value, label }) => (
  <div className={styles.wrepper}>
    <div className={styles.wrepperLeftImg}>{icon}</div>
    <div className={styles.wrepperRight}>
      <div>{value}</div>
      <div>{label}</div>
    </div>
  </div>
)

const CardItem = ({ icon, value, label, todayNew, todayClose }) => (
  <div className={styles.card}>
    <div className={styles.cardLeft}>
      <div className={styles.topImg}>{icon}</div>
      <div className={styles.wrepperRight}>
        <div className={styles.cardRightBlock}>{value}</div>
        <div className={styles.zb}>{label}</div>
      </div>
    </div>
    <div className={styles.cardRight}>
      <div className={styles.cardRightBlock}>
        <div className={styles.zb}>{todayNew}</div>
        <div>今日新增</div>
      </div>
      <div className={`${styles.cardRightBlock} ${styles.bootm}`}>
        <div className={styles.zb}>{todayClose}</div>
        <div>今日关闭</div>
      </div>
    </div>
  </div>
)

const TopSection = ({ statistics }) => {
  return (
    <div className={styles.headerLeft}>
      <div className={styles.top}>
        <StatsItem icon={<Clients />} value={statistics?.monitorClientCount} label="监控客户数" />
      </div>

      <div className={styles.contentCardList}>
        <CardItem
          icon={<IconRedLightwarning />}
          value={statistics?.warnClientCount}
          label="预警客户"
          todayNew={statistics?.newWarnClientCount}
          todayClose={statistics?.closeWarnClientCount}
        />
        <CardItem
          icon={<IconYellowLightWarning />}
          value={statistics?.opClientCount}
          label="发生舆情客户"
          todayNew={statistics?.newOpClientCount}
          todayClose={statistics?.closeOpClientCount}
        />
      </div>
    </div>
  )
}

export default observer(TopSection)
