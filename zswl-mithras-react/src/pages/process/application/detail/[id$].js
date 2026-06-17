import { observer } from '@zswl/admin'
import PrepareDetail from './PrepareDetail'
import Detail from '../../Detail/index'

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
