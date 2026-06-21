import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form

function AfterLeaseLevel5ClassifyEdit({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={'编辑'} store={store.editModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'建议分类'}
          name={'classifyResult'}
          rules={[{ required: true, message: '请选择' }]}
        >
          <Select placeholder="请输入" allowClear options={'assetClassifySuggestEnum'} />
        </Item>
        <Item name="id" hidden>
          <Input></Input>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(AfterLeaseLevel5ClassifyEdit)
