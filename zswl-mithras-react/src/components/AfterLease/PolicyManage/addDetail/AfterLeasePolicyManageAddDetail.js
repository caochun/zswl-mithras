import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useMemo } from 'react'
import styles from './index.less'
import Store from './store'
import TipsModal from '../../Level5Classify/TipsModal'
import Base from '../Base/AfterLeasePolicyManageBase'

const AfterLeasePolicyManageAddDetail = ({ params: { id }, compareData }) => {
  const store = useMemo(() => {
    return new Store({})
  }, [])

  return (
    <Page store={store.page} params={{ id }} header={null}>
      <div className={styles.wrap}>
        <Base id={id} store={store} />
      </div>
      <TipsModal store={store.tipsModal} />
    </Page>
  )
}
export default observer(AfterLeasePolicyManageAddDetail)
