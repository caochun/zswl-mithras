import { Amount } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import store from './store'
import { Input, DatePicker, InputNumber } from 'antd'

const { Item } = Form

//新增地址
function CustomerExternalPublicInfoZdwModal() {
  return (
    <Modal title={'新增中登网'} store={store.zDwModal} okText={'确定'} width={480} destroyOnClose>
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} preserve={false}>
        <Item label={'交易业务类型'} name={'tradeBusinessType'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'授信机构'} name={'creditOrg'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'金额（亿元）'} name={'amount'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'登记日期'} name={'regDate'}>
          <DatePicker style={{ width: '100%' }} />
        </Item>
        <Item label={'登记到期日'} name={'regExpireDate'}>
          <DatePicker style={{ width: '100%' }} />
        </Item>
        <Item label={'期限（年）'} name={'term'}>
          <Input placeholder={'请输入'} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CustomerExternalPublicInfoZdwModal)
