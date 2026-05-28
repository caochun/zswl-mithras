import { observer } from '@zswl/admin'
import { Bifrost } from '@/components'

function Index() {
  return <Bifrost path={'/dictionary'} />
}

export default observer(Index)
