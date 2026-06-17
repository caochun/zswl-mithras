import { formateCard } from '@/utils'
import { observer } from '@zswl/admin'
import { Form, Modal, Select, App } from '@zswl/components'
import { Input } from 'antd'
import { useMemo, useState } from 'react'
import Api from '@/api/contract/contractAccount'

const { Item } = Form

function Index({ store, leaseTypes, projCode }) {
  console.log('projCode: ', projCode)
  const [form] = Form.useForm()
  const { isCreate } = store
  const [backList, setBankList] = useState([])
  const [accountNames, setAccountNames] = useState([])

  const contractAccountPayeeTypeEnum = App.getData().optionsType?.contractAccountPayeeTypeEnum
  // 若租赁类型为直租，在合同管理详情页面-收款账户模块维护收款账户时，可选择新增收款方为「卖方」的账户信息；
  const newContractAccountPayeeTypeEnum = useMemo(() => {
    if (leaseTypes === 'zhi_zu') {
      return contractAccountPayeeTypeEnum.filter((item) => item.label !== '乙方')
    }
    return contractAccountPayeeTypeEnum.filter((item) => item.label !== '卖方')
  }, [leaseTypes])

  const getBankAccountList = async () => {
    const res = await Api.getBankAccountList({})
    setBankList(res)
    return res
  }

  const onBankChange = (value) => {
    if (value) {
      const current = backList.filter((item) => item.id === value)[0]
      form.setFieldsValue({
        accountNum: current.accountNumber,
        accountAddress: current.accountBank,
        accountName: current.accountName,
        bankAccountId: value,
      })
    }
  }

  const onPayeeTypeChange = async (value) => {
    if (value === 'YI') {
      const list = await Api.baseDataContractAccountList({ projCode })
      setAccountNames(list)
      form.setFieldsValue({
        accountName: list?.[0]?.accountName,
        accountNum: undefined,
        accountAddress: undefined,
      })
    } else {
      form.setFieldsValue({
        accountName: undefined,
        accountNum: undefined,
        accountAddress: undefined,
      })
    }
  }

  return (
    <Modal
      title={`${isCreate ? '新建' : '编辑'}收款账户`}
      store={store.$createModal}
      destroyOnClose
    >
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item label={'收款方'} name={'payeeType'} rules={[{ required: true, message: '请选择！' }]}>
          <Select options={newContractAccountPayeeTypeEnum} onChange={onPayeeTypeChange} />
        </Item>
        <Item dependencies={['payeeType']} noStyle>
          {({ getFieldValue }) => {
            const payeeType = getFieldValue('payeeType')
            if (payeeType === 'JIA') {
              return (
                <>
                  <Item
                    label={'账户名称'}
                    name={'accountName'}
                    rules={[{ required: true, message: '请选择账户名称！' }]}
                  >
                    <Select
                      onChange={onBankChange}
                      options={getBankAccountList}
                      fieldNames={{ label: 'accountName', value: 'id' }}
                    ></Select>
                  </Item>
                  <Item
                    label={'银行账号'}
                    name={'accountNum'}
                    getValueFromEvent={(e) => formateCard(e.target.value)}
                    rules={[{ required: true, message: '请输入银行账号！' }]}
                  >
                    <Input disabled />
                  </Item>
                  <Item
                    label={'开户行'}
                    name={'accountAddress'}
                    rules={[{ required: true, message: '请输入开户行！' }]}
                  >
                    <Input disabled />
                  </Item>
                </>
              )
            } else if (payeeType === 'YI') {
              return (
                <>
                  <Item
                    label={'账户名称'}
                    name={'accountName'}
                    rules={[{ required: true, message: '请选择账户名称！' }]}
                  >
                    {/* <ApiSelect
                      api={Api.baseDataContractAccountList}
                      params={{ projCode }}
                      fieldNames={{ label: 'accountName', value: 'accountName' }}
                    /> */}
                    <Select
                      options={accountNames}
                      fieldNames={{ label: 'accountName', value: 'accountName' }}
                    />
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
                </>
              )
            } else if (payeeType === 'OTHER' || payeeType === 'SELLER') {
              return (
                <>
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
                </>
              )
            }
          }}
        </Item>
        <Item hidden name="id">
          <Input />
        </Item>
        <Item hidden name="bankAccountId">
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
