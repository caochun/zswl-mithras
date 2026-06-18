import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'
import { formateCard } from '@/utils'


const { Item } = Form
const selectData = [
  { label: '是', value: true },
  { label: '否', value: false },
]
//新增关联企业
function Index({ store }) {
  return (
    <Modal
      title={'银行账户'}
      store={store.bankAccountModal}
      okText={'确定'}
      width={480}
      destroyOnClose
    >
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} preserve={false}>
        {/* <Item
          label={'银行账户'}
          getValueFromEvent={(e) => formateCard(e.target.value)}
          name={'accountNumber'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Input placeholder={'请输入'} />
        </Item> */}
        <BankAccount.Item name={'accountNumber'} label={'银行账户'}></BankAccount.Item>
        <Item
          label={'账户名称'}
          name={'accountName'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Input placeholder={'请输入'} />
        </Item>
        <Item
          label={'开户行'}
          name={'accountBank'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'是否主账号'} name={'mainAccount'}>
          <Select options={selectData} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
