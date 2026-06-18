import { observer } from '@zswl/admin'
import { Button, Form, Modal, Select } from '@zswl/components'
import { DatePicker, Input, Space, Tag, Tooltip } from 'antd'
import { FormAmount } from '@/components/Form'
import { ClientSelect, OrgTreeSelect } from '@/components'
import { useState } from 'react'
import _ from 'lodash'
import contractApi from '@/api/contract/baseInfo'
import bankFlowProcessingCenterApi from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'
import moment from 'moment'
import { ExclamationCircleOutlined, InfoCircleFilled } from '@ant-design/icons'
import bankFlowCapitalApi from '@/api/budget/flowCenter/bankFlowCapitalApi'
import { amountFormat, formatPercent } from '@/utils'

const defaultValue = {
  shouldPayAmount: undefined,
  shouldPayTime: undefined,
  noPayAmount: undefined,
}
const { Item } = Form
function CashFlowModal({ store, writeOffType, sideType }) {
  const { editType } = store.offModal.getInitialValues() ?? {}
  const disabled = editType === 'EDIT'

  const isProj = sideType === 'PROJ_SIDE'
  const [financeList, setFinanceList] = useState([])
  const modalForm = store.offModal.getFormStore()

  const { writeOffedAmount, setWriteOffedAmount } = store
  const [contractList, setContractList] = useState([])
  const isPayment = writeOffType === 'PAYMENT'
  const [cashFlowCodeList, setCashFlowCodeList] = useState([])

  const ProjItem = () => {
    const cashFlowItemOptions = isPayment
      ? 'paymentWriteOffOrderEnum'
      : 'collectionWriteOffOrderEnum'
    const onContractChange = async (contract) => {
      modalForm.setFieldsValue({
        cashFlowItem: undefined,
        shouldPayAmount: undefined,
        ...defaultValue,
      })
    }
    const clientNameChange = async (client) => {
      const res = await contractApi.postContractList({ clientId: client.value, pageSize: 9999 })
      setContractList(res.list)
      modalForm.setFieldsValue({
        contract: undefined,
        cashFlowItem: undefined,
        shouldPayAmount: undefined,
        ...defaultValue,
      })
    }
    const cashFlowItemChange = async () => {
      const { cashFlowItem, contract } = modalForm.getFieldsValue()
      if (!cashFlowItem?.value) return
      modalForm.setFieldsValue({
        cashFlowCode: undefined,
        ...defaultValue,
      })
      try {
        const res = await bankFlowProcessingCenterApi.postBankCenterProjectCodeList({
          contractId: contract.value,
          cashFlowItem: cashFlowItem.value,
          writeOffType,
        })
        setCashFlowCodeList(
          res.map((item) => {
            return { label: item, value: item }
          })
        )
        if (res && res.length === 1) {
          modalForm.setFieldsValue({
            cashFlowCode: res[0],
          })
          setTimeout(() => {
            cashFlowCodeChange()
          }, 100)
        }
      } catch (e) {}
    }

    const cashFlowCodeChange = async () => {
      const { cashFlowItem, contract, cashFlowCode } = modalForm.getFieldsValue()
      modalForm.setFieldsValue({
        ...defaultValue,
      })
      const res = await bankFlowProcessingCenterApi.postAmountDetail({
        contractId: contract.value,
        cashFlowItem: cashFlowItem.value,
        writeOffType,
        cashFlowCode,
      })
      modalForm.setFieldsValue({
        ...res,
        shouldPayTime: res.shouldPayTime ? moment(res.shouldPayTime) : undefined,
      })
    }
    return (
      <>
        <Item label={'客户名称'} name={'client'} rules={[{ required: true, message: '请选择！' }]}>
          <ClientSelect
            canJump={false}
            labelInValue
            functionCode="clientlist-5"
            disabled={disabled}
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
            disabled={disabled}
          />
        </Item>
        <Item
          label={'现金流项目'}
          name={'cashFlowItem'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select
            options={cashFlowItemOptions}
            onChange={cashFlowItemChange}
            labelInValue
            disabled={disabled}
          />
        </Item>
        <Item noStyle dependencies={['cashFlowItem', 'contract']}>
          {({ getFieldValue }) => {
            const cashFlowItem = getFieldValue('cashFlowItem')
            if (cashFlowItem?.value) {
              return (
                <Item
                  label={'现金流编号'}
                  name={'cashFlowCode'}
                  rules={[{ required: true, message: '请选择！' }]}
                >
                  <Select
                    options={cashFlowCodeList}
                    disabled={disabled}
                    onChange={cashFlowCodeChange}
                  />
                </Item>
              )
            }
          }}
        </Item>
      </>
    )
  }
  const FinancialItem = () => {
    const cashFlowItemOptions = isPayment
      ? 'financePaymentWriteOffOrderEnum'
      : 'financeCollectionWriteOffOrderEnum'

    const orgChange = async (value, record) => {
      const res = await bankFlowCapitalApi.postInfoList(record)
      const newList = res.map(({ financingCode, receiptRepayBaseId, ...rest }) => ({
        label: financingCode,
        value: receiptRepayBaseId,
        ...rest,
      }))
      modalForm.setFieldsValue({
        receiptRepayBaseId: undefined,
        cashFlowItem: undefined,
        cashFlowCode: undefined,
        ...defaultValue,
      })
      setFinanceList(newList)
    }
    const financeChange = async () => {
      const { cashFlowItem, receiptRepayBaseId } = modalForm.getFieldsValue()
      if (!cashFlowItem?.value || !receiptRepayBaseId?.value) return
      const res = await bankFlowCapitalApi.postCashflowList({
        receiptRepayBaseId: receiptRepayBaseId?.value,
        cashFlowItem: cashFlowItem?.value,
      })
      modalForm.setFieldsValue({
        cashFlowCode: undefined,
        ...defaultValue,
      })
      setCashFlowCodeList(res)
    }
    const cashFlowItemCodeChange = async (value) => {
      const { noPayAmount, shouldPayAmount, writeOffedAmount, shouldPayTime } =
        cashFlowCodeList.find((v) => v.cashFlowCode === value)
      modalForm.setFieldsValue({
        noPayAmount,
        shouldPayAmount,
        writeOffedAmount,
        shouldPayTime: shouldPayTime ? moment(shouldPayTime) : undefined,
      })
    }
    return (
      <>
        <Item name={'orgId'} label="机构名称" rules={[{ required: true, message: '请选择！' }]}>
          <Select
            options={async () => {
              const res = await bankFlowCapitalApi.postOrgList({})
              return res.map((v) => ({ ...v, value: `${v.name}-${v.id}` }))
            }}
            fieldNames={{ label: 'name', value: 'value' }}
            onChange={orgChange}
            placeholder="请选择！"
            disabled={disabled}
            labelInValue
          />
        </Item>
        <Item
          label={'借据编号'}
          name={'receiptRepayBaseId'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select
            labelInValue
            onChange={financeChange}
            placeholder="请选择！"
            disabled={disabled}
            optionLabelProp="label"
          >
            {financeList.map(({ label, value, actualLoanDate, financingAmount }) => (
              <Option value={value} label={label}>
                <Space className="demo-option-label-item">
                  {label} <Tag color="green">{actualLoanDate}</Tag>
                  <Tag color="gold">{amountFormat(formatPercent(financingAmount))} 元</Tag>
                </Space>
              </Option>
            ))}
          </Select>
        </Item>
        <Item
          label={'现金流项目'}
          name={'cashFlowItem'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select
            options={cashFlowItemOptions}
            onChange={financeChange}
            labelInValue
            placeholder="请选择！"
            disabled={disabled}
          />
        </Item>
        <Item
          label={'现金流编号'}
          name={'cashFlowCode'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select
            options={cashFlowCodeList}
            fieldNames={{ label: 'cashFlowCode', value: 'cashFlowCode' }}
            onChange={cashFlowItemCodeChange}
            disabled={disabled}
            placeholder="请选择！"
          />
        </Item>
      </>
    )
  }

  return (
    <Modal
      title={`${disabled ? '编辑' : '新增'}现金流项目`}
      store={store.offModal}
      okText={'确定'}
      width={720}
      destroyOnClose
      afterClose={() => {
        setWriteOffedAmount([])
        setContractList([])
        setFinanceList([])
        setCashFlowCodeList([])
        modalForm.resetFields()
      }}
    >
      <Form labelCol={{ span: 8 }}>
        <div style={{ maxHeight: 500, overflowY: 'auto', padding: '0 10px' }}>
          <Form.Item name={'modelId'} hidden>
            <Input />
          </Form.Item>
          {isProj ? ProjItem() : FinancialItem()}
          <FormAmount.Item
            disabled
            label={isPayment ? '应付金额（元）' : '应收金额（元）'}
            name={'shouldPayAmount'}
          />
          <Form.Item name={'shouldPayTime'} label={isPayment ? '应付日期' : '应收日期'}>
            <DatePicker disabled style={{ width: '100%' }} />
          </Form.Item>
          <div style={{ display: 'flex' }}>
            <FormAmount.Item
              disabled
              label={isPayment ? '未付金额（元）' : '未收金额（元）'}
              name={'noPayAmount'}
              isRequired={false}
              style={{ width: '100%' }}
            />
            {!!writeOffedAmount?.length && (
              <Tooltip
                title={(writeOffedAmount ?? []).map((v) => (
                  <div>{v}</div>
                ))}
              >
                <ExclamationCircleOutlined style={{ margin: '12px' }} />
              </Tooltip>
            )}
          </div>
          <FormAmount.Item
            label={'本次核销金额（元）'}
            name={'thisWriteOffAmount'}
            isRequired={true}
            min={-Infinity}
          />
        </div>
      </Form>
    </Modal>
  )
}

export default observer(CashFlowModal)
