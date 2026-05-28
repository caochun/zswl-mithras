import { history, observer, getQuery } from '@zswl/admin'
import { App, Button, Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import styles from './index.less'
import Store from './store'
import TipsModal from '../../level5Classify/Component/Process/TipsModal'
import Base from '../Base'

const Index = ({ params: { id }, compareData }) => {
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
export default observer(Index)
