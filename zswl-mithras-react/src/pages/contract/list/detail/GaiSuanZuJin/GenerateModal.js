import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { DatePicker } from 'antd'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={`生成现金流`} store={store} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'计划起租日'}
          name={'planStartDate'}
          rules={[{ required: true, message: '请选择日期!' }]}
        >
          <DatePicker placeholder={'请选择'} style={{ width: 160 }} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
