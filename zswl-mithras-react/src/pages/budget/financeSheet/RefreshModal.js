import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { DatePicker } from 'antd'

const Index = ({ store }) => {
  return (
    <Modal
      width={400}
      afterClose={store.onClose}
      store={store.$refreshModal}
      propsBy={(data) => {
        return {
          title: '刷新',
        }
      }}
      destroyOnClose
    >
      <Form>
        <Form.Item
          label="请选择需刷新的月份"
          name="date"
          rules={[{ required: true, message: '请选择' }]}
        >
          <DatePicker picker="month" />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
