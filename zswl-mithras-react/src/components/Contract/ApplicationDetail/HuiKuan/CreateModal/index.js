import { formateCard } from '@/utils'
import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()
  const { isCreate } = store

  return (
    <Modal title={`${isCreate ? '新建' : '编辑'}`} store={store.$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'回款方式'}
          name={'repayWay'}
          rules={[{ required: true, message: '请输入回款方式！' }]}
        >
          <Select options={'rePayType'} />
        </Item>
        <Item
          label={'账户名称'}
          name={'accountName'}
          rules={[{ required: true, message: '请输入账户名称！' }]}
        >
          <Input />
        </Item>
        <Item
          label={'银行账号'}
          name={'accountNum'}
          getValueFromEvent={(e) => formateCard(e.target.value)}
          rules={[{ required: true, message: '请输入银行账号！' }]}
        >
          <Input />
        </Item>
        <Item
          label={'开户行'}
          name={'accountAddress'}
          rules={[{ required: true, message: '请输入开户行！' }]}
        >
          <Input />
        </Item>
        <Item hidden name="id">
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
