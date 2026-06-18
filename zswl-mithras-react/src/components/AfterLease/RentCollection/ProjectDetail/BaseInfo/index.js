import CantractDetailBaseInfo from '@/components/Contract/BaseInfo'
import styles from '../index.less'

const BaseInfo = ({ contractId }) => {
  return (
    <div className={styles.directionWrap}>
      <CantractDetailBaseInfo contractId={contractId} canEditFlag={false} />
    </div>
  )
}
export default BaseInfo
