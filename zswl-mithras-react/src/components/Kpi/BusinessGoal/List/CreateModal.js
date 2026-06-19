import { observer } from '@zswl/admin'
import { Modal, Form, Select } from '@zswl/components'
import { DatePicker } from 'antd'
const { Item } = Form

const Index = ({ store }) => {
  return (
    <Modal title={'创建'} width={400} store={store.createModal}>
      <Form labelCol={{ span: 6 }}>
        <Item label="年度" name="year">
          <DatePicker picker="year" style={{ width: '100%' }}></DatePicker>
        </Item>
        <Item label="是否启用" name="status">
          <Select options="earlyWarningState"></Select>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
