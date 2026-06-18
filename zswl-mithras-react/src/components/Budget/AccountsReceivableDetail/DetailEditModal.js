import { Input, DatePicker } from 'antd'

import { observer } from '@zswl/admin'
import { FormAmount } from '@/components/Form'
import { Form, Modal, Select } from '@zswl/components'

/**
 * 应收逾期集成结算单编辑弹窗组件
 */
const DetailEditModal = observer(({ store }) => {
  const isEdit = store.addModal.getInitialValues()?.id
  return (
    <Modal
      store={store.addModal}
      title={!isEdit ? '新增逾期集成单' : '编辑逾期集成单'}
      destroyOnClose
      width={600}
    >
      <Form labelAlign="right" labelCol={4}>
        <Form.Item
          label="客户名称"
          name="clientName"
          rules={[{ required: true, message: '请输入客户名称' }]}
        >
          <Input placeholder="请输入客户名称" disabled />
        </Form.Item>

        <Form.Item
          label="合同编号"
          name="contractCode"
          rules={[{ required: true, message: '请输入合同编号' }]}
        >
          <Input placeholder="请输入合同编号" disabled />
        </Form.Item>

        <Form.Item
          label="现金流编号"
          name="collectionCode"
          rules={[{ required: true, message: '请输入现金流编号' }]}
        >
          <Input placeholder="请输入现金流编号" disabled />
        </Form.Item>

        <Form.Item
          label="款项内容"
          name="paymentNumber"
          rules={[{ required: true, message: '请输入款项内容' }]}
        >
          <Select options="overduePaymentNumberEnum" placeholder="请输入款项内容" />
        </Form.Item>

        <Form.Item
          label="科目"
          name="accounttypeNumber"
          rules={[{ required: true, message: '请输入科目' }]}
        >
          <Input placeholder="请输入科目" />
        </Form.Item>

        <Form.Item
          label="约定收款条件"
          name="recordPaymentTerms"
          rules={[{ required: true, message: '请输入约定收款条件' }]}
        >
          <Input placeholder="请输入约定收款条件" />
        </Form.Item>

        <Form.Item
          label="约定收款日期"
          name="recordDueDate"
          rules={[{ required: true, message: '请选择约定收款日期' }]}
          transform={(value) => (value ? moment(value).format('yyyy-MM-DD') : null)}
        >
          <DatePicker
            style={{ width: '100%' }}
            placeholder="请选择约定收款日期"
            format="YYYY-MM-DD"
          />
        </Form.Item>

        <Form.Item
          label="单据日期"
          name="recordBillDate"
          rules={[{ required: true, message: '请选择单据日期' }]}
          transform={(value) => (value ? moment(value).format('yyyy-MM-DD') : null)}
        >
          <DatePicker style={{ width: '100%' }} placeholder="请选择单据日期" format="YYYY-MM-DD" />
        </Form.Item>

        <Form.Item
          label="行业收款周期"
          name="collectionCycle"
          rules={[{ required: true, message: '请输入行业收款周期' }]}
        >
          <Input placeholder="请输入行业收款周期" />
        </Form.Item>
        <FormAmount.Item label="应收金额（元）" name="receAmount" initFormat={1} />
        <Form.Item name="id" hidden>
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  )
})

export default DetailEditModal
