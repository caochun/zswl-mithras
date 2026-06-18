import { Modal, Form } from '@zswl/components'
import { DatePicker } from 'antd'
import Amount from '@/components/Amount'
import AmountNumber from '@/components/AmountNumber'
import { rules } from '@/utils'

const { Item } = Form

const Index = ({ store }) => {
  return (
    <Modal title={'创建合同'} store={store.fundConfirmModal} okText={'确定'} destroyOnClose>
      <Form labelCol={{ span: 6 }} preserve={false}>
        <Item name="amount" label="交易金额" rules={[rules.required()]}>
          <Amount>
            <AmountNumber />
          </Amount>
        </Item>
        <Item label={'评估日期'} name={'assessDate'} rules={[rules.required()]}>
          <DatePicker style={{ width: '100%' }} format="yyyy-MM-DD" />
        </Item>
      </Form>
    </Modal>
  )
}
export default Index
