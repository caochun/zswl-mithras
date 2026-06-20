import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import Api from '@/api/credit/groupCreditEstablishVersionApi'
import { VersionTable } from '@/components/Table'

function Index({ params: { id } }) {
  return (
    <Page header={null}>
      <VersionTable
        params={{ mainId: id }}
        getListApi={Api.postVersionList}
        toDifferentInfo={store.toDifferentInfo}
      />
    </Page>
  )
}

export default observer(Index)
