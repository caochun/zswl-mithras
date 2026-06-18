import { history, observer } from '@zswl/admin'
import { BlackGrayDetail as Detail } from '@/components/BlackGray/BlackGrayEntries'

function Id({ params, path, query }) {
  return <Detail params={params} query={query} path={path} source="EXTERNAL_APPROVAL" />
}

export default observer(Id)
