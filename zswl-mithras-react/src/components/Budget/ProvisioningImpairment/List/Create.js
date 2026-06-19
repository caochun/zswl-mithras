import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { DatePicker } from 'antd'
import store from './store'

const { Item } = Form

const Index = () => {
  return (
    <Modal title={'创建'} store={store.createModal} okText={'确定'} destroyOnClose width={400}>
      <Form preserve={false}>
        <Item name="provisionDate" label="月份" rules={[{ required: true, message: '请选择' }]}>
          <DatePicker picker="month" style={{ width: '100%' }} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
