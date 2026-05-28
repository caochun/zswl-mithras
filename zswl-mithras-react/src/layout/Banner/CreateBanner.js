import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form

const Index = ({ store }) => {
  const [form] = Form.useForm()

  return (
    <Modal title={'新增公告'} store={store.$createBanner} destroyOnClose>
      <Form form={form} labelCol={{ span: 4 }} preserve={false}>
        <Item name="title" label="标题" rules={[{ required: true, message: '请输入' }]}>
          <Input placeholder="请输入标题" />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
