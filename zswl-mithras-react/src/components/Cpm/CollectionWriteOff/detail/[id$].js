import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import DetailTitle from '../../DetailTitle'
import CashFlowInfo from './Components/CashFlowInfo'
import Contract from './Components/Contract'
import RecordList from './Components/RecordList'
import styles from './index.less'
import Store from './store'
import { useMemo } from 'react'

const CollectionWriteOffDetail = ({ params: { id } }) => {
  console.log('打印：', { id })
  const store = useMemo(() => new Store(), [])
  return (
    <Page store={store} params={{ id }} current="详情" header={null}>
      <div className={styles.wrap}>
        <DetailTitle
          title={store.page.getData().collectionCode}
          status={store.page.getData().writeOffStatus}
          matchKey="collectionWriteOffStatusEnum"
        />
        <Contract store={store} />
        <CashFlowInfo store={store} />
        <RecordList store={store} />
      </div>
    </Page>
  )
}
export default observer(CollectionWriteOffDetail)
