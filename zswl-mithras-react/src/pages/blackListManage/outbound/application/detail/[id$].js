import { history, observer } from '@zswl/admin'
import { BlackGrayBreakDetail as Detail } from '@/components/BlackGray/BlackGrayEntries'

function Id({ params, path, query }) {
  return <Detail params={params} query={query} path={path} />
}

export default observer(Id)
