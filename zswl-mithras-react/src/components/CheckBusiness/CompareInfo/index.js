import { Modal, Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import CompareTable from './CompareTable'
import OptionList from './OptionList'
import { Space } from 'antd'

const Index = ({ store, needOption = true }) => {
  const { canEditOpinion } = store

  return (
    <Modal
      store={store.compareModal}
      title="工商信息校验"
      width={1100}
      footer={
        canEditOpinion() ? (
          <Space>
            <Button type="primary" onClick={store.compareModal.submit}>
              确定
            </Button>
            <Button onClick={store.compareModal.close}>取消</Button>
          </Space>
        ) : null
      }
    >
      <CompareTable store={store} />
      {needOption && <OptionList store={store} />}
    </Modal>
  )
}

export default observer(Index)
