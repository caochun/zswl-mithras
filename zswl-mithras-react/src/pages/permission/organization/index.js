import { observer } from '@zswl/admin'
import { Bifrost } from '@/components'

function Index() {
  return <Bifrost path={'/permission/organization'} />
}

export default observer(Index)
