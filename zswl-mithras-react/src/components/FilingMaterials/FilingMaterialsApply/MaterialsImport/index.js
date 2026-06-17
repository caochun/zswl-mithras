import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Button, Space } from 'antd'


function Index({ store }) {
  const [isModalOpen, setIsModalOpen] = useState(false)
  const showModal = () => {
    setIsModalOpen(true)
  }

  const handleOk = () => {
    setIsModalOpen(false)
  }

  const handleCancel = () => {
    setIsModalOpen(false)
  }
  return (
    <>
      <Modal
        title={`资料引入`}
        onOk={handleOk}
        onCancel={handleCancel}
        open={isModalOpen}
        destroyOnClose
        width={800}
      >
        12334
      </Modal>
    </>
  )
}

export default observer(Index)
