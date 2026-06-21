import { observer } from '@zswl/admin'
import styles from './index.less'

const StatsItem = ({ icon, value, label }) => (
  <div className={styles.statsWrapper} style={{ marginLeft: 20 }}>
    <div className={styles.statsLeftImg}>{icon}</div>
    <div className={styles.statsRight}>
      <div>{value}</div>
      <div>{label}</div>
    </div>
  </div>
)

const CardItem = ({ icon, value, label, todayNew, todayClose }) => (
  <div className={styles.cardItem}>
    <div className={styles.cardLeft}>
      <div className={styles.cardTopImg}>{icon}</div>
      <div className={styles.cardRight}>
        <div className={styles.cardValue}>{value}</div>
        <div className={styles.cardLabel}>{label}</div>
      </div>
    </div>
    <div className={styles.cardRight}>
      <div className={styles.cardBlock}>
        <div className={styles.cardNew}>{todayNew}</div>
        <div>今日新增</div>
      </div>
      <div className={`${styles.cardBlock} ${styles.cardBottom}`}>
        <div className={styles.cardClose}>{todayClose}</div>
        <div>今日关闭</div>
      </div>
    </div>
  </div>
)

const TopSection = ({ statistics }) => {
  const { totalMonitorClient, totalWarnClient, opinionCard = [] } = statistics || {}
  const getCardData = (code) => {
    return opinionCard.find((item) => item.cardCode === code) || {}
  }

  const redCard = getCardData('RED')
  const yellowCard = getCardData('YELLOW')
  const opinionData = getCardData('OPINION')

  return (
    <div className={styles.headerLeft}>
      <div className={styles.headerTop}>
        <StatsItem
          icon={<img src="/public/assets/risk/monitoringAlertList/clients.svg" />}
          value={totalMonitorClient}
          label="监控客户数"
        />
        <StatsItem
          icon={<img src="/public/assets/risk/monitoringAlertList/warningCustomers.svg" />}
          value={totalWarnClient}
          label="预警客户数"
          className={styles.statsTow}
        />
      </div>

      <div className={styles.cardList}>
        <CardItem
          icon={<img src="/public/assets/risk/monitoringAlertList/iconRedLightWarning.svg" />}
          value={redCard.amount}
          label="红灯预警"
          todayNew={redCard.todayAdd}
          todayClose={redCard.todayClose}
        />
        <CardItem
          icon={<img src="/public/assets/risk/monitoringAlertList/iconYellowLightWarning.svg" />}
          value={yellowCard.amount}
          label="黄灯预警"
          todayNew={yellowCard.todayAdd}
          todayClose={yellowCard.todayClose}
        />
        <CardItem
          icon={<img src="/public/assets/risk/monitoringAlertList/publicSentiment.svg" />}
          value={opinionData.amount}
          label="舆情"
          todayNew={opinionData.todayAdd}
          todayClose={opinionData.todayClose}
        />
      </div>
    </div>
  )
}

export default observer(TopSection)
