import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import { DatePicker } from 'antd'
import moment from 'moment'
import styles from './index.less'
const { Item } = Form

const BudgetBusinessAgingCreateModal = ({ store }) => {
  // 只能选每个月的最后一天
  function disabledDate(current) {
    const endOfMonth = moment(current).endOf('month')
    return !current.isSame(endOfMonth, 'day')
  }
  return (
    <Modal title={'创建'} width={600} store={store.createModal} destroyOnClose>
      <Form labelCol={{ span: 6 }}>
        <Item
          label="账龄截止日"
          name="deadline"
          required
          rules={[{ required: true, message: '账龄截止日不能为空' }]}
          transform={(val) => ({
            deadline: val && moment(val).format('YYYY-MM-DD'),
          })}
        >
          <DatePicker style={{ width: '100%' }} disabledDate={disabledDate}></DatePicker>
        </Item>

        <div className={styles.tips}> 提示：根据苍穹账龄表推送机制，有以下规则限制：</div>
        <div className={styles.tips}>1、账龄截止日只可选择每月的最后一天；</div>
        <div className={styles.tips}>
          2、应收需推送期初金额和期末金额，并且需要计算当期的增加额和减少额，因此账龄业务表需按时间顺序进行创建；
        </div>
      </Form>
    </Modal>
  )
}

export default observer(BudgetBusinessAgingCreateModal)
