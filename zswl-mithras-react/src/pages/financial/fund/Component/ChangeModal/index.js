import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'

const { Item } = Form

function ChangeModal({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={'贷后变更'} store={store.$changeModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'变更类型'}
          name={'changeType'}
          rules={[{ required: true, message: '请选择变更类型！' }]}
        >
          <Select options={'fundFinancingChangeSubTypeEnum'} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(ChangeModal)
