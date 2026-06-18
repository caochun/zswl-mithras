import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { Form, Modal } from '@zswl/components'
const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={`补充协议上传`} store={store.$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'补充协议'}
          name={'files'}
          rules={[{ required: true, message: '请上传文件!' }]}
        >
          <DataUpload accept="*" maxCount={100}></DataUpload>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
