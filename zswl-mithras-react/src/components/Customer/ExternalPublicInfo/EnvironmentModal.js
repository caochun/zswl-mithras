import { Amount } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import store from './store'
import { Input, DatePicker, InputNumber } from 'antd'

const { Item } = Form

//新增地址
function CustomerExternalPublicInfoEnvironmentModal() {
  return (
    <Modal
      title={'新增环保处罚'}
      store={store.environmentalModal}
      okText={'确定'}
      width={480}
      destroyOnClose
    >
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} preserve={false}>
        <Item label={'处罚日期'} name={'penaltyTime'}>
          <DatePicker style={{ width: '100%' }} />
        </Item>
        <Item label={'处罚文书号'} name={'punishNumber'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'处罚事由'} name={'reason'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'处罚结果'} name={'result'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'处罚金额(万元)'} name={'amount'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'处罚单位'} name={'departmentName'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'数据来源'} name={'source'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'执行情况'} name={'info'}>
          <Input placeholder={'请输入'} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CustomerExternalPublicInfoEnvironmentModal)
