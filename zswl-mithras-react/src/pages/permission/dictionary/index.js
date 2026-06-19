import { observer } from '@zswl/admin'
import { PermissionBifrost as Bifrost } from '@/components/Permission/BifrostEntries'

function Index() {
  return <Bifrost path={'/dictionary'} />
}

export default observer(Index)
