import IconFont from '@/components/Icon'
import styles from './index.less'

/**
 * 大标题
 * @param {*String} title 标题名称
 * @param {*String} iconType iconFont icon的名称
 * @returns
 */

const DashboardTitle = ({ title, iconType }) => {
  return (
    <div className={styles.wrap}>
      <IconFont className={styles.icon} type={iconType} />
      <div className={styles.title}>{title}</div>
    </div>
  )
}

export default DashboardTitle
