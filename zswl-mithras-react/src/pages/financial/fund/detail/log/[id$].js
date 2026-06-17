import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import Api from '@/api/financial/fundApi'
import { VersionTable } from '@/components'

function Index(props) {
  const { id } = props.params ?? {}

  return (
    <Page header={null}>
      <VersionTable
        params={{ mainId: id }}
        getListApi={Api.getVersionList}
        toDifferentInfo={store.toDifferentInfo}
      />
    </Page>
  )
}

export default observer(Index)
