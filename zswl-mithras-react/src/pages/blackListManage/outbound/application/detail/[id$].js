import { history, observer } from '@zswl/admin'
import Detail from '@/pages/blackListManage/components/BreakDetail'

function Id({ params, path, query }) {
  return <Detail params={params} query={query} path={path} />
}

export default observer(Id)
