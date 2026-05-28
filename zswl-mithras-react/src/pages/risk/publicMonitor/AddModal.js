import { ClientSelect } from '@/components'
import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form
const Index = ({ store }) => {
  const { $addModal } = store
  const [form] = Form.useForm()
  const handleChange = (val, option) => {
    if (val) {
      store.addValues = {
        id: option.id,
        clientId: option.clientCode,
        clientName: option.clientName,
        creditCode: option.uscCode,
      }
      form.setFieldValue('creditCode', option.uscCode)
    }
  }
  return (
    <Modal title={'选择客户'} store={$addModal} okText={'确定'} destroyOnClose width={580}>
      <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 15 }}>
        <Item name="clientName" label="客户名称" rules={[{ required: true }]}>
          <ClientSelect
            canJump={false}
            functionCode="clientlist-4"
            onChange={handleChange}
            params={{ clientType: 'CORPORATION', clientStatus: 'TAKE_EFFECT' }}
          ></ClientSelect>
        </Item>
        <Item name="creditCode" label="统一社会信用代码" rules={[{ required: true }]}>
          <Input placeholder="请输入" disabled></Input>
        </Item>
      </Form>
    </Modal>
  )
}
export default observer(Index)
