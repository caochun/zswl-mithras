import { observer } from '@zswl/admin'
import { Button, Modal } from '@zswl/components'
import styles from './index.less'

function CpmPaymentApplicationTipsModal({ store, ...rest }) {
  const { metricNames = [] } = store.getInitialValues() || {}
  return (
    <Modal
      title="风险策略助手提示"
      footer={[
        <Button key="cancel" onClick={() => store.close()}>
          放弃申请
        </Button>,
      ]}
      store={store}
      {...rest}
    >
      <div className={styles.flexBox}>
        <div className={styles.img}>
          <div className={styles.backImg}></div>
        </div>
        <div className={styles.content}>
          <p>监测到存在放款风险</p>
          <p>申请后,该行业目前{metricNames.join('、')}已超限额。存在风险，不予放款！</p>
        </div>
      </div>
    </Modal>
  )
}

export default observer(CpmPaymentApplicationTipsModal)
