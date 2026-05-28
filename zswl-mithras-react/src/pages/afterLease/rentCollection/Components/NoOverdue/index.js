import styles from './index.less'
const NoOverdue = () => {
  return (
    <div className={styles.wrap}>
      <div className={styles.content}>
        <img src="/public/assets/image/overdue.png" />
        <div className={styles.text}>恭喜！您的客户没有发生逾期</div>
      </div>
    </div>
  )
}

export default NoOverdue
