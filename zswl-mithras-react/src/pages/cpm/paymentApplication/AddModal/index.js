import { ClientSelect } from '@/components'
import { amountFormat, getInputNumberAmountProps } from '@/utils'
import { observer } from '@zswl/admin'
import { App, Form, Modal } from '@zswl/components'
import { Input, Select } from 'antd'
import { useEffect, useState } from 'react'
import Api from '../api'
import store from '../store'

const AddModal = () => {
  const [form] = Form.useForm()

  const [contractList, setContractList] = useState([])
  const clientChange = async (val) => {
    const res = await Api.getContractList({ clientId: val.value })
    if (res) {
      setContractList(res ?? [])
    } else {
      setContractList([])
    }
    form.setFieldsValue({
      contractCode: undefined,
      payables: undefined,
      planedPaidAmount: undefined,
      amountPaid: undefined,
      amountApplied: undefined,
      remainingApplyAmount: undefined,
      planedPaidDate: undefined,
    })
  }
  const onContractCodeChange = (val) => {
    const data = contractList.find((item) => item.contractId === val.value)
    if (data) {
      const {
        payables,
        planedPaidAmount,
        amountPaid,
        amountApplied,
        remainingApplyAmount,
        planedPaidDate,
      } = data
      form.setFieldsValue({
        payables,
        planedPaidAmount: amountFormat(planedPaidAmount / 10000),
        amountPaid: amountFormat(amountPaid / 10000),
        amountApplied: amountFormat(amountApplied / 10000),
        remainingApplyAmount: amountFormat(remainingApplyAmount / 10000),
        planedPaidDate,
      })
    }
  }

  const layout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 17 },
  }
  return (
    <Modal title={'创建付款申请'} store={store.createModal} destroyOnClose>
      <Form form={form} {...layout}>
        <Form.Item
          label="客户名称"
          name="clientId"
          rules={[{ required: true, message: '请输入客户名称！' }]}
        >
          <ClientSelect
            labelInValue
            canJump={false}
            functionCode="clientlist-7"
            onChange={clientChange}
          />
        </Form.Item>
        <Form.Item
          label="合同编号"
          name="contractCode"
          rules={[{ required: true, message: '请输入合同编号！' }]}
        >
          <Select
            showSearch
            options={contractList}
            labelInValue={true}
            onChange={onContractCodeChange}
            fieldNames={{ label: 'contractCode', value: 'contractId' }}
            filterOption={(input, option) => {
              return option.contractCode?.includes(input)
            }}
          />
        </Form.Item>
        <Form.Item label="应付项目" name="payables">
          <Input disabled placeholder="自动生成" />
        </Form.Item>
        <Form.Item label="合同生效日期" name="planedPaidDate">
          <Input disabled placeholder="自动生成" />
        </Form.Item>

        <Form.Item name="planedPaidAmount" label="计划付款金额(元)">
          <Input disabled placeholder="自动生成" />
        </Form.Item>
        <Form.Item name="amountPaid" label="已付金额(元)">
          <Input disabled placeholder="自动生成" />
        </Form.Item>
        <Form.Item label="已申请通过金额(元)" name="amountApplied">
          <Input disabled placeholder="自动生成" />
        </Form.Item>
        <Form.Item label="剩余可申请金额(元)" name="remainingApplyAmount">
          <Input disabled placeholder="自动生成" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
export default observer(AddModal)
