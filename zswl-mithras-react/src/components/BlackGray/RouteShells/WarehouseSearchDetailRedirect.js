import { Host } from '@zswl/admin'

function Index({ params: { id } }) {
  return <Host pathname={`/blackListManage/warehouse/mainTask/detail/${id}`} />
}

export default Index
