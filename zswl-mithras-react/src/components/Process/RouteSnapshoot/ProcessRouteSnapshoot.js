import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import ProcessSnapshoot from '../Snapshoot/ProcessSnapshot'

function ProcessRouteSnapshoot({ params: { id } }) {
  return (
    <Page header={null}>
      <ProcessSnapshoot id={id} />
    </Page>
  )
}

export default observer(ProcessRouteSnapshoot)
