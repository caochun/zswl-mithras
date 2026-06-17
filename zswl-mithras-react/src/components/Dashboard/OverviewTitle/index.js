import styles from './index.less'

const Index = ({ title, extra }) => {
  return (
    <div className={styles.wrap}>
      <div className={styles.wrap_title}>{title}</div>
      <div className={styles.wrap_extra}>{extra}</div>
    </div>
  )
}

export default Index
