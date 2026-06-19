import { observer } from '@zswl/admin'
import { ProcessDetail as Detail } from '@/components/Process/DetailEntries'
import { ProcessPrepareDetail as PrepareDetail } from '@/components/Process/PrepareDetailPageEntries'

function Index(props) {
  const {
    params: { id },
    query,
    pathname,
  } = props
  const { tab } = query
  return (
    <div>
      {tab === 'prepare' ? (
        <PrepareDetail id={id} />
      ) : (
        <Detail id={id} query={query} pathname={pathname} />
      )}
    </div>
  )
}

export default observer(Index)
