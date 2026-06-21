import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { debounce as _debounce } from 'lodash'
import { Input, DatePicker, Checkbox } from 'antd'


const { Item } = Form

function FinancialFundCostDetailCreateModal({ store, detail }) {
  const [form] = Form.useForm()
  const isYT = detail.businessType === 'SYNDICATIONS'
  return (
    <Modal
      propsBy={(data) => {
        const title = `${data ? '编辑' : '新增'}费用明细`
        return {
          title,
        }
      }}
      store={store.$createModal}
      destroyOnClose
    >
      <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} preserve={false}>
        <Item label={'融资机构'} name="organization" required hidden={!isYT}>
          <Select
            labelInValue
            options={detail?.organizationInfoList ?? []}
            fieldNames={{ label: 'organizationName', value: 'organizationId' }}
          />
        </Item>
        <Item
          label={'费用类型'}
          name={'expenseType'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select options={'financingFeeType'} />
        </Item>
        <Item name="amount" label="金额 （元）" required>
          <FormAmount />
        </Item>
        <Item label={'支付方式'} name="paymentMethod" required>
          <Input />
        </Item>
        <Item
          label={'支付时间'}
          name="payDate"
          required
          transform={(val) => val && moment(val).format('yyyy-MM-DD')}
        >
          <DatePicker />
        </Item>
        <Item label={'备注'} name="remark">
          <Input.TextArea rows={3} maxLength={500} />
        </Item>
        <Item name="id" hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(FinancialFundCostDetailCreateModal)
