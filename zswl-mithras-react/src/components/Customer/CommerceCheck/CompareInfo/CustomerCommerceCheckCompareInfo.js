import { Modal } from '@zswl/components'
import { observer } from '@zswl/admin'
import CompareTable from './CompareTable'

const CustomerCommerceCheckCompareInfo = ({ store }) => {
  return (
    <Modal store={store.compareModal} title="工商信息校验" width={1100} footer={null}>
      <CompareTable store={store}></CompareTable>
    </Modal>
  )
}

export default observer(CustomerCommerceCheckCompareInfo)
