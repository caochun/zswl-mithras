import { observer } from '@zswl/admin'
import Bifrost from '@/components/Bifrost'

function Index() {
  return <Bifrost path={'/dictionary'} />
}

export default observer(Index)
