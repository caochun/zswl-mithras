import { observer } from '@zswl/admin'
import ProcessDetail from '../Detail'
import ProcessPrepareDetail from '../PrepareDetail'

function ProcessRouteDetail({ params: { id }, query, pathname }) {
  if (query?.tab === 'prepare') {
    return <ProcessPrepareDetail id={id} />
  }

  return <ProcessDetail id={id} query={query} pathname={pathname} />
}

export default observer(ProcessRouteDetail)
