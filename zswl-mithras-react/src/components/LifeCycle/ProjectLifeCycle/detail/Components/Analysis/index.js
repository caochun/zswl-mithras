import styles from '../../index.less'

const Analysis = () => {
  return (
    <div className={styles.moduleWrap} style={{ marginBottom: 16 }}>
      <div className={styles.title}>项目分析看板</div>
      <div className={styles.emptyWrap}>
        <img src={'/public/assets/image/empty.png'} />
        <div className={styles.text}>即将上线，敬请期待</div>
      </div>
    </div>
  )
}

export default Analysis
