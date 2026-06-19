import { observer } from '@zswl/admin'
import Detail from '../Manage/BreakDetail'

function Id({ params, path, query }) {
  return <Detail params={params} query={query} path={path} />
}

export default observer(Id)
