import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import ProcessSnapshot from '../Snapshot/ProcessSnapshot'

function ProcessRouteSnapshot({ params: { id } }) {
  return (
    <Page header={null}>
      <ProcessSnapshot id={id} />
    </Page>
  )
}

export default observer(ProcessRouteSnapshot)
