import CreditSummary from './CreditSummary'
// import HistoricalCredit from './HistoricalCredit'
import Classifaction from './Classifaction'
import styles from './styles.less' // 使用 CSS Modules

export default function Creditinformation({ store, id }) {
  return (
    <div id={id}>
      <div className={styles['title']}>授信信息</div>
      <div className={styles.hader}>
        <CreditSummary store={store}></CreditSummary>
        {/* <HistoricalCredit store={store}></HistoricalCredit> */}
        <Classifaction store={store}/>
      </div>
    </div>
  )
}
