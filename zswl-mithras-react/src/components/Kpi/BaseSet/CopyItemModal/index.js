import { Modal, Form } from '@zswl/components'
import { observer } from '@zswl/admin'
import { DatePicker, Input } from 'antd'
import moment from 'moment'

const Index = ({ store }) => {
  const { effectMonth } = store.copyItemModal.getInitialValues() ?? {}

  return (
    <Modal store={store.copyItemModal} title={'复制到'} width={400}>
      <Form>
        <Form.Item
          name="effectMonth"
          label="生效月份"
          rules={[{ required: true }]}
          transform={(val) => val && moment(val).endOf('month').format('yyyy-MM-DD')}
        >
          <DatePicker picker="month"></DatePicker>
        </Form.Item>
        <Form.Item name="id" hidden>
          <Input></Input>
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
