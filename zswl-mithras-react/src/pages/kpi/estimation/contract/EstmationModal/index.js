import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import { DatePicker } from 'antd'
import moment from 'moment'

const Index = ({ store }) => {
  const disabledDate = (current) => {
    return false
    // 首次初始化时间点
    return current && current < moment('2024-07').endOf('month')
  }
  return (
    <Modal title="绩效测算" store={store.calculationModal}>
      <Form>
        <Form.Item
          label="月份"
          name="calculateDate"
          transform={(val) => {
            return val && moment(val).format('YYYY-MM-DD')
          }}
        >
          <DatePicker disabledDate={disabledDate} picker="month" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
export default observer(Index)
