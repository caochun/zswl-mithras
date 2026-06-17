import styles from './index.less'
const NoData = ({ text }) => {
  return (
    <div className={styles.wrap}>
      <img src="/public/assets/image/noData.svg" />
      <div className={styles.text}>{text || '未到达该阶段，暂无数据'}</div>
    </div>
  )
}
export default NoData
