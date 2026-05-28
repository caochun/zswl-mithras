import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import { DatePicker } from 'antd'
import { rules } from '@/utils'
import moment from 'moment'
const { Item } = Form

const Index = ({ store }) => {
  return (
    <Modal title="计提利息" store={store.chooseMonthModal} width={400} destroyOnClose>
      <Form preserve={false}>
        <Item name="months" label="计提月份" rules={[rules.required('请选择')]}>
          <DatePicker picker="month" />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
