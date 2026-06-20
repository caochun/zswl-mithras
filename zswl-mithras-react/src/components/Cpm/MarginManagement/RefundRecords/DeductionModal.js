import { Amount } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Input, InputNumber, DatePicker } from 'antd'
import { Modal, Form } from '@zswl/components'
import store from './store'

function Index() {
  const [form] = Form.useForm()

  return (
    <Modal
      title={'保证金抵扣'}
      store={store.deductionModal}
      okText={'确定'}
      destroyOnClose
      footer={null}
    >
      <Form labelCol={{ span: 5 }} wrapperCol={{ span: 18 }} preserve={false} form={form}>
        <Form.Item name={'collectionType'} label="付款方式">
          <Input disabled />
        </Form.Item>
        <Form.Item name={'rentCollectionCode'} label="抵扣期项">
          <Input disabled />
        </Form.Item>
        <Form.Item name={'collectionAmount'} label="抵扣金额">
          <Amount>
            <InputNumber style={{ width: '100%' }} disabled />
          </Amount>
        </Form.Item>
        <Form.Item name={'collectionDate'} label="抵扣日期">
          <DatePicker style={{ width: '100%' }} disabled />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
