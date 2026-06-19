import { observer } from '@zswl/admin'
import { PermissionBifrost as Bifrost } from '@/components/Permission/BifrostEntries'

function Index() {
  return <Bifrost path={'/permission/organization'} />
}

export default observer(Index)
