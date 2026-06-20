import { Host } from '@zswl/admin'

function WarehouseMainTaskDetailRedirect({ params: { id } }) {
  return <Host pathname={`/blackListManage/warehouse/mainTask/detail/${id}`} />
}

export default WarehouseMainTaskDetailRedirect
