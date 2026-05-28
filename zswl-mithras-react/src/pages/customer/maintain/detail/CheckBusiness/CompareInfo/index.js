import { Modal, Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import CompareTable from './CompareTable'
import OptionList from './OptionList'
import { Space } from 'antd'

const Index = ({ store }) => {
  const { canEditOpinion } = store

  return (
    <Modal store={store.compareModal} title="工商信息校验" width={1100} footer={null}>
      <CompareTable store={store}></CompareTable>
      {/* <OptionList store={store}></OptionList> */}
    </Modal>
  )
}

export default observer(Index)
