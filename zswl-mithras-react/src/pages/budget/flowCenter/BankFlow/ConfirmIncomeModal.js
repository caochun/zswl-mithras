import { observer } from '@zswl/admin'
import { useState } from 'react'
import { Button, Form, Modal, Select } from '@zswl/components'
import { Tag, Space } from 'antd'
import { FormAmount } from '@/components/Form'
import { OrgTreeSelect } from '@/components'
import { ClientSelect } from '@/components/Select'
import contractInfoApi from '@/api/budget/contractInfoApi'
import { amountFormat, formatPercent } from '@/utils'
import bankFlowProcessingCenterApi from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'

const { Item } = Form
const { Option } = Select

function Index({ store }) {
  const [contractList, setContractList] = useState([])
  const [receiptCodeList, setReceiptCodeList] = useState([])

  const modalForm = store.confirmIncomeModal.getFormStore()

  const onContractChange = async (contract) => {
    const res = await bankFlowProcessingCenterApi.postCenterReceiptCode({
      contractId: contract.value,
    })
    setReceiptCodeList(res)
    modalForm.setFieldsValue({
      receiptCode: undefined,
    })
  }
  const clientNameChange = async (client) => {
    const res = await contractInfoApi.postContractList({
      clientId: client.value,
      pageSize: 9999,
    })
    setContractList(res.list)
    modalForm.setFieldsValue({
      contract: undefined,
      receiptCode: undefined,
    })
  }

  return (
    <Modal
      title={`确认收入`}
      store={store.confirmIncomeModal}
      okText={'确定'}
      width={600}
      destroyOnClose
    >
      <Form labelCol={{ span: 6 }}>
        <Item label={'客户名称'} name={'client'} rules={[{ required: true, message: '请选择！' }]}>
          <ClientSelect
            canJump={false}
            labelInValue
            functionCode="clientlist-5"
            onChange={clientNameChange}
          ></ClientSelect>
        </Item>
        <Item
          label={'合同编号'}
          name={'contract'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select
            fieldNames={{ label: 'contractCode', value: 'id' }}
            onChange={onContractChange}
            options={contractList}
            placeholder="请选择！"
            labelInValue
          />
        </Item>
        <Item dependencies={['contract']} noStyle>
          {({ getFieldValue }) => {
            const contract = getFieldValue('contract')
            if (!contract) return null
            return (
              <Item
                label={'借据编号'}
                name={'receiptId'}
                rules={[{ required: true, message: '请选择！' }]}
              >
                <Select
                  placeholder="请选择！"
                  optionLabelProp="label"
                  notFoundContent={'未找到借据编号'}
                >
                  {receiptCodeList.map(({ receiptCode, receiptId, paidInDate, paidInAmount }) => (
                    <Option value={receiptId} label={receiptCode}>
                      <Space className="demo-option-label-item">
                        {receiptCode} <Tag color="green">{paidInDate}</Tag>
                        <Tag color="gold">{amountFormat(formatPercent(paidInAmount))} 元</Tag>
                      </Space>
                    </Option>
                  ))}
                </Select>
              </Item>
            )
          }}
        </Item>
        <Item
          label={'现金流项目'}
          name={'cashFlowItem'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select options={'confirmIncomeEnum'} />
        </Item>
        <FormAmount.Item
          label={'本次核销金额(元)'}
          name={'writeOffAmount'}
          isRequired
          style={{ width: '100%' }}
        />
      </Form>
    </Modal>
  )
}

export default observer(Index)
