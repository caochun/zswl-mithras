import { App, Form, Modal } from '@zswl/components'
import { Input, InputNumber, Select } from 'antd'
import { observer } from '@zswl/admin'
import { formateCard, getInputNumberAmountProps, validatorMax } from '@/utils'
import { BankAccount } from '@/components/Form'

const PaymentDetailsModal = observer(({ activeData, store }) => {
  const [form] = Form.useForm()

  const layout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 19 },
  }

  return (
    <Modal
      propsBy={(data) => {
        return {
          title: (data ? '编辑' : '新增') + '支付明细',
        }
      }}
      store={store.createApplicationModal}
      destroyOnClose
    >
      <Form form={form} {...layout}>
        <Form.Item
          label="支付金额"
          name="paymentAmount"
          rules={[
            {
              required: true,
              message: '请输入支付金额！',
            },
            // validatorAmount,
            // validatorMax,
          ]}
        >
          <InputNumber
            style={{ width: '100%' }}
            {...getInputNumberAmountProps()}
            placeholder="请输入支付金额！"
          />
        </Form.Item>
        <Form.Item
          label="支付方式"
          name="paymentMethod"
          rules={[
            {
              required: true,
              message: '请选择支付方式！',
            },
          ]}
        >
          <Select options={App.getData().optionsType.paymentMethod} placeholder="请选择支付方式" />
        </Form.Item>
        <Form.Item label="附言" name="postscript">
          <Input.TextArea placeholder="请输入附言" />
        </Form.Item>
        <Form.Item
          label="对方账户名"
          name="oppositeAccountName"
          rules={[
            {
              required: true,
              message: '请选择对方账户名！',
            },
          ]}
        >
          <Input placeholder="请输入对方账户名" />
        </Form.Item>
        <BankAccount.Item label="银行账号" name="oppositeAccount"></BankAccount.Item>

        <Form.Item
          name="oppositeAccountBank"
          label="开户行"
          rules={[
            {
              required: true,
              message: '请输入开户行！',
            },
          ]}
        >
          <Input placeholder="请输入开户行！" />
        </Form.Item>
      </Form>
    </Modal>
  )
})

export default PaymentDetailsModal
