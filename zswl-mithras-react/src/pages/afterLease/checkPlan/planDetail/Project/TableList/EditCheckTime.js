import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { DatePicker, Input } from 'antd'

const { Item } = Form

function CreateModal({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={'选择检查日期'} store={store} okText={'确定'} destroyOnClose width={580}>
      <Form
        form={form}
        labelCol={{ span: 6 }}
        preserve={false}
        initialValues={{
          scene: 'riskManager',
        }}
      >
        <Item name="id" hidden>
          <Input />
        </Item>
        <Item name="scene" hidden>
          <Input />
        </Item>
        <Item
          label={'检查日期'}
          name="checkTime"
          rules={[{ required: true, message: '请选择' }]}
          transform={(value) => {
            return moment(value).format('YYYY-MM-DD')
          }}
        >
          <DatePicker />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CreateModal)
