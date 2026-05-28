import { observer } from '@zswl/admin'
import { Form, Input, Modal, Select } from '@zswl/components'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={'删除'} store={store.deleteModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 8 }} preserve={false}>
        <Item label={'删除原因'} name={'reason'} rules={[{ required: true, message: ' 请输入' }]}>
          <Input.TextArea rows={3} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
