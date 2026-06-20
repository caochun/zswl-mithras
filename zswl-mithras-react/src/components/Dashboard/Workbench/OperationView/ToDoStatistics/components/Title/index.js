import IconFont from '@/components/Icon'
import styles from './index.less'

const Index = ({ title, iconType }) => {
  return (
    <div className={styles.wrap}>
      <div className={styles.wrap2}>
        <IconFont type={iconType} className={styles.icon}></IconFont>
        <span className={styles.title}>{title}</span>
      </div>
      <div className={styles.unit}>单位(个)</div>
    </div>
  )
}

export default Index
