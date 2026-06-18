import { Input, DatePicker } from 'antd'
import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { FormAmount } from '@/components/Form'
import { ClientSelect } from '@/components/Select'

/**
 * 应收逾期集成结算单编辑弹窗组件
 */
const OverdueEditModal = observer(({ store }) => {
  const isEdit = store.overdueModal.getInitialValues()?.id
  return (
    <Modal
      store={store.overdueModal}
      title={!isEdit ? '新增结算单' : '编辑结算单'}
      destroyOnClose
      width={500}
    >
      <Form labelAlign="right" labelCol={4}>
        <Form.Item
          label="客户名称"
          name="clientId"
          rules={[{ required: true, message: '请输入客户名称' }]}
        >
          <ClientSelect
            placeholder="请输入客户名称"
            canJump={false}
            onChange={store.onSelectClient}
            disabled={isEdit}
          />
        </Form.Item>

        <Form.Item
          label="合同编号"
          name="contract"
          rules={[{ required: true, message: '请输入合同编号' }]}
        >
          <Select
            options={store.contractList}
            placeholder="请输入合同编号"
            onChange={store.onSelectContract}
            disabled={isEdit}
            labelInValue
          />
        </Form.Item>

        <Form.Item
          label="现金流编号"
          name="collection"
          rules={[{ required: true, message: '请输入现金流编号' }]}
        >
          <Select
            options={store.flowList}
            placeholder="请输入现金流编号"
            disabled={isEdit}
            labelInValue
          />
        </Form.Item>

        <Form.Item
          label="单据日期"
          name="recordBillDate"
          rules={[{ required: true, message: '请选择单据日期' }]}
          transform={(value) => (value ? moment(value).format('YYYY-MM-DD') : '')}
        >
          <DatePicker style={{ width: '100%' }} placeholder="请选择单据日期" format="YYYY-MM-DD" />
        </Form.Item>
        <Form.Item
          label="结算日期"
          name="settlementDate"
          rules={[{ required: true, message: '请选择结算日期' }]}
          transform={(value) => (value ? moment(value).format('YYYY-MM-DD') : '')}
        >
          <DatePicker style={{ width: '100%' }} placeholder="请选择结算日期" format="YYYY-MM-DD" />
        </Form.Item>
        <Form.Item
          label="结算记录的凭证记账日期"
          name="voucherAccountDate"
          rules={[{ required: true, message: '请选择结算记录的凭证记账日期' }]}
          transform={(value) => (value ? moment(value).format('YYYY-MM-DD') : '')}
        >
          <DatePicker
            style={{ width: '100%' }}
            placeholder="请选择结算记录的凭证记账日期"
            format="YYYY-MM-DD"
          />
        </Form.Item>
        <Form.Item
          label="结算关系"
          name="settlementRelation"
          rules={[{ required: true, message: '请选择结算关系' }]}
        >
          <Select placeholder="请选择结算关系" options={'overdueSettlementRelationEnum'} />
        </Form.Item>
        <FormAmount.Item label="结算金额（元）" name="settlementAmount" initFormat={1} />
        <Form.Item name={'id'} hidden />
      </Form>
    </Modal>
  )
})

export default OverdueEditModal
