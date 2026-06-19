import styles from './index.less'

const CommonNoData = ({ text = '未到达该阶段，暂无数据' }) => {
  return (
    <div className={styles.wrap}>
      <img src="/public/assets/image/noData.svg" />
      <div className={styles.text}>{text}</div>
    </div>
  )
}

export default CommonNoData
