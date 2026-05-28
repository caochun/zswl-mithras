import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import ProjectAllocateList from '../ProjectAllocateList'

const Index = ({ store }) => {
  return (
    <Modal store={store.beforeAllocateInfoModal} footer={null} title="分润比（变更前）" width={600}>
      <ProjectAllocateList.Detail value={store.beforeAllocateInfoData} />
    </Modal>
  )
}
export default observer(Index)
