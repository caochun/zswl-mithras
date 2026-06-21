import { observer } from '@zswl/admin'
import { Button, Modal } from '@zswl/components'
import styles from './index.less'

function ProjectEstablishmentRiskTipsModal({ store, ...rest }) {
  const { metricNames = [] } = store.getInitialValues() || {}
  return (
    <Modal title="风险策略助手提示" store={store} {...rest}>
      <div className={styles.flexBox}>
        <div className={styles.img}>
          <div className={styles.backImg}></div>
        </div>
        <div className={styles.content}>
          <p>监测到存在立项风险</p>
          <p>申请后,该行业目前{metricNames.join('、')}已超限额。存在风险，请关注并注意！</p>
        </div>
      </div>
    </Modal>
  )
}

export default observer(ProjectEstablishmentRiskTipsModal)
