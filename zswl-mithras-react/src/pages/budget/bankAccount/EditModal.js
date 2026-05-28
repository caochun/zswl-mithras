import BankAccount from '@/components/Form/BankAccount'
import FormAmount from '@/components/Form/FormAmount'
import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { DatePicker, Input } from 'antd'

const EditModal = ({ store }) => {
  const layout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 17 },
  }
  return (
    <Modal
      store={store}
      propsBy={(data) => {
        return {
          title: (!data ? '新增' : data?.noEdit ? '' : '编辑') + '我方账户',
          footer: data?.noEdit ? <></> : undefined,
        }
      }}
      destroyOnClose
    >
      <Form {...layout}>
        <Form.Item
          label="账户名称"
          name="accountName"
          rules={[{ required: true, message: '请选择账户名称！' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="开户银行"
          name="accountBank"
          rules={[{ required: true, message: '请选择开户银行！' }]}
        >
          <Input></Input>
        </Form.Item>
        <BankAccount.Item label="银行账号" name="accountNumber"></BankAccount.Item>

        <Form.Item
          label="账户性质"
          name="accountType"
          rules={[{ required: true, message: '请选择账户性质！' }]}
        >
          <Select options={'baseDataBankAccountTypeEnum'} />
        </Form.Item>
        <Form.Item
          label="是否贷款账户"
          name="isLoan"
          rules={[{ required: true, message: '请选择是否贷款账户！' }]}
        >
          <Select options={'yesOrNo'} />
        </Form.Item>
        <Form.Item
          label="账户状态"
          name="accountStatus"
          rules={[{ required: true, message: '请选择账户状态！' }]}
        >
          <Select options={'baseDataBankAccountStatusEnum'} />
        </Form.Item>
        <Form.Item label="开户时间" name="openingDate">
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item label="币种" name="currency">
          <Select options={'currencyType'} />
        </Form.Item>
        <Form.Item label="账户余额" name="accountBalance">
          <FormAmount />
        </Form.Item>
        <Form.Item label="备注" name="remark">
          <Input.TextArea />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
