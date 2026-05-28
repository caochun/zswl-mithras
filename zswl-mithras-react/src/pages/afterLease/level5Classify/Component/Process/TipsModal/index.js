import { observer } from '@zswl/admin'
import { Button, Modal } from '@zswl/components'
import styles from './index.less'

function Index({ store, ...rest }) {
  const { metricNames = [] } = store?.getInitialValues() || {}
  return (
    <Modal title="提示" store={store} {...rest}>
      <div className={styles.flexBox}>
        <div className={styles.img}>
          <div className={styles.backImg}></div>
        </div>
        <div className={styles.content}>
          <p>系统初分中，请耐心等待...</p>
        </div>
      </div>
    </Modal>
  )
}

export default observer(Index)
