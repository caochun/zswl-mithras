import CreditSummary from './CreditSummary'
import CreditClassification from './CreditClassification'
import styles from './styles.less' // 使用 CSS Modules

export default function CreditInformation({ store, id }) {
  return (
    <div id={id}>
      <div className={styles['title']}>授信信息</div>
      <div className={styles.hader}>
        <CreditSummary store={store}></CreditSummary>
        <CreditClassification store={store} />
      </div>
    </div>
  )
}
