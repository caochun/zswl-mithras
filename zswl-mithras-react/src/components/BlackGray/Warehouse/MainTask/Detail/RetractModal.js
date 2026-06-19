import { observer } from '@zswl/admin'
import { Modal, Button, Form, Input } from '@zswl/components'

function RetractModal({ store }) {
  return (
    <Modal
      title={'任务退回'}
      store={store.retractModal}
      width={500}
      destroyOnClose
      footer={[
        <Button
          key="submit"
          type="primary"
          onClick={() => {
            store.subManageRetract()
          }}
        >
          确定
        </Button>,
      ]}
    >
      <Form store={store.retractForm} cache={false}>
        <Form.Item
          name={'suggest'}
          label="退回原因"
          rules={[{ required: true, message: '请填写退回原因!' }]}
        >
          <Input.TextArea rows={4} maxLength={1000} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(RetractModal)
