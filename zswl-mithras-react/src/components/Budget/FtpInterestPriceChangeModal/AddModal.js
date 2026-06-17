import { useEffect, useState } from 'react'
import { message } from 'antd'
import { Button, Modal, Form, Select } from '@zswl/components'
import contractApi from '@/api/contract/baseInfo'
import { ClientSelect } from '@/components'
import ftpInterestChangeApi from '@/api/budget/pricing/ftpInterestChangeApi'
import { observer } from '@zswl/admin'
import moment from 'moment'

const { Item } = Form

const AddModal = ({ store, setDetailList, detailList }) => {
  const form = store.getFormStore()
  const clientId = form.getFieldValue('clientId')
  const [contractList, setContractList] = useState([])
  const [receiptList, setReceiptList] = useState([])
  const getReceiptList = async (contractId) => {
    const res = await ftpInterestChangeApi.postReceiptListByContract({ contractId })
    setReceiptList(res)
  }

  const handleContractChange = (value) => {
    form.setFieldsValue({
      receiptCode: undefined,
    })
    setReceiptList([])
    if (value) {
      getReceiptList(value?.value)
    }
  }
  const clientNameChange = async (client) => {
    const res = await contractApi.postContractList(
      { clientId: client.value, pageSize: 9999 },
      'ftpInterestContractBaseInfoList'
    )
    setContractList(res.list)
    form.setFieldsValue({
      contract: undefined,
      receiptCode: undefined,
    })
  }

  const onSubmit = async () => {
    const { clientId, contract, receiptCode } = await form.validateFields()
    const hasSome = detailList.some((item) => item.receiptId === receiptCode?.value)
    if (hasSome) {
      message.error('该借据已存在')
      return
    }
    const { ftpAssessInfo } = receiptList.find((item) => item.id === receiptCode?.value)
    const { effectDate, ...rest } = ftpAssessInfo ?? {}
    const newValues = {
      clientId: clientId?.value,
      clientName: clientId?.label,
      contract: contract?.value,
      contractCode: contract?.label,
      receiptId: receiptCode?.value,
      receiptCode: receiptCode?.label,
      effectDate: moment().format('YYYY-MM-DD'),
      ...rest,
    }

    setDetailList((prev) => [...prev, newValues])
    store.close()
  }

  return (
    <Modal
      title="FTP计息变更-增加"
      store={store}
      destroyOnClose
      footer={[
        <Button key="cancel" onClick={store.close}>
          取消
        </Button>,
        <Button type="primary" onClick={onSubmit}>
          确定
        </Button>,
      ]}
    >
      <Form>
        <Item
          label="客户名称"
          name="clientId"
          key="clientId"
          rules={[{ required: true, message: '请选择客户名称' }]}
        >
          <ClientSelect
            canJump={false}
            labelInValue
            functionCode="ftpinterestclientlist"
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
            labelInValue
            onChange={handleContractChange}
            options={contractList}
            placeholder="请选择！"
            disabled={!form.getFieldValue('clientId')}
          />
        </Item>

        <Item
          name="receiptCode"
          label="借据编号"
          rules={[{ required: true, message: '请选择借据编号' }]}
        >
          <Select
            placeholder="请选择借据编号"
            labelInValue
            options={receiptList}
            fieldNames={{ label: 'receiptCode', value: 'id' }}
            disabled={!form.getFieldValue('contract')}
          />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(AddModal)
