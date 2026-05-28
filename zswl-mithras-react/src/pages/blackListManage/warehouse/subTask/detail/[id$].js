import { observer, Host } from '@zswl/admin'

function Index({ params: { id } }) {
  return <Host pathname={`/blackListManage/warehouse/mainTask/detail/${id}`} sub />
}
export default observer(Index)
