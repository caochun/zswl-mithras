import CantractDetailBaseInfo from '@/pages/contract/list/detail/BaseInfo'
import styles from '../index.less'

const BaseInfo = ({ contractId }) => {
  return (
    <div className={styles.directionWrap}>
      <CantractDetailBaseInfo contractId={contractId} canEditFlag={false} />
    </div>
  )
}
export default BaseInfo
