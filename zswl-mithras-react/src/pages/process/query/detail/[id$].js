import { ProcessDetail as Detail } from '@/components/Process/DetailEntries'
import { observer } from '@zswl/admin'

function Index({ params: { id }, query, pathname }) {
  return <Detail id={id} query={query} pathname={pathname} />
}

export default observer(Index)
