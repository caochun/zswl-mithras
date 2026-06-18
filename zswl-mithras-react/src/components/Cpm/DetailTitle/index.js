import { observer } from '@zswl/admin'
import { App } from '@zswl/components'
import classNames from 'classnames'
import styles from './index.less'
const DetailTitle = ({ title, status, matchKey }) => {
  return (
    <div className={styles.detailTitle}>
      {title}
      <div className={classNames(styles.writeOffStatus, styles[`${status}`])}>
        {App.matchOption(matchKey, status).label}
      </div>
    </div>
  )
}
export default observer(DetailTitle)
