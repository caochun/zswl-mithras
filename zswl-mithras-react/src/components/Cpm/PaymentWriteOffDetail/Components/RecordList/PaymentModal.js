import { Amount } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Button, Form, Modal, Select } from '@zswl/components'
import { DatePicker, Input } from 'antd'
import { useEffect, useState } from 'react'
import AmountNumber from '../../../AmountNumber'
import { rules } from '@/utils'
import api from '@/api/cpm/payment/paymentWriteOffDetailApi'

const PaymentModal = ({ store }) => {
  const [ourBankList, setOurBankList] = useState()

  const { defaultOppositeAccount, defaultOppositeAccountBank, defaultOppositeAccountName } =
    store.actualDetail

  const [form] = Form.useForm()
  const layout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 19 },
  }
  useEffect(() => {
    getBankList()
  }, [])

  const getBankList = async () => {
    const res = await api.getClientBankList({})
    setOurBankList(res ?? [])
  }
  const bankChange = (val) => {
    const data = ourBankList.find((item) => item.id === val.value)
    if (data) {
      const { accountBank, accountName, accountNumber, id } = data
      form.setFieldsValue({
        ourAccountBank: accountBank,
        ourAccountId: {
          label: accountName,
          value: id,
        },
      })
    }
  }
  return (
    <Modal
      propsBy={(data) => {
        const { _pageStatus } = data
        const _title = _pageStatus === 'create' ? '新增' : _pageStatus === 'edit' ? '编辑' : '查看'
        return {
          title: _title + '付款明细',
          footer: ['create', 'edit'].includes(data._pageStatus) ? undefined : <></>,
        }
      }}
      store={store.collectionModal}
      destroyOnClose
    >
      {(data) => {
        const canEdit = ['create', 'edit'].includes(data._pageStatus)
        return (
          <Form form={form} {...layout} disabled={!canEdit}>
            <Form.Item
              label="付款方式"
              name="paymentMethod"
              rules={[
                {
                  required: true,
                  message: '请选择付款类型！',
                },
              ]}
            >
              <Select options={'paymentMethod'} placeholder="请选择类型！" />
            </Form.Item>
            <Form.Item
              label="资金来源"
              name="capitalSource"
              rules={[
                {
                  required: true,
                  message: '请选择资金来源！',
                },
              ]}
            >
              <Select options={'capitalSource'} placeholder="请选择资金来源！" />
            </Form.Item>
            <Form.Item dependencies={['capitalSource']} noStyle>
              {({ getFieldValue }) => {
                const val = getFieldValue('capitalSource')
                if (val=="BANK_LOAN") {
                  return (
                    <>
                      <Form.Item
                        label="融资编号"
                        name="financingCode"
                        rules={[
                          {
                            required: true,
                            message: '请选择融资编号！',
                          },
                        ]}
                      >
                        <Select options={'financingCode'} placeholder="请选择融资编号！" />
                      </Form.Item>
                    </>
                  )
                }
              }}
            </Form.Item>
            <Form.Item label="实付金额" name="paidInAmount" required rules={[rules.required()]}>
              <Amount>
                <AmountNumber></AmountNumber>
              </Amount>
            </Form.Item>
            <Form.Item
              label="实付日期"
              name="paidInDate"
              rules={[
                {
                  required: true,
                  message: '请选择实付日期！',
                },
              ]}
            >
              <DatePicker placeholder="请选择实付日期！" style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item
              label="我方账户名"
              name="ourAccountId"
              rules={[
                {
                  required: true,
                  message: '请选择银行账号！',
                },
              ]}
            >
              <Select
                options={ourBankList}
                labelInValue
                fieldNames={{ label: 'accountName', value: 'id' }}
                disabled
              />
            </Form.Item>
            <Form.Item label="银行账号" name="ourAccountNumber">
              <Select
                onChange={bankChange}
                options={ourBankList}
                labelInValue
                fieldNames={{ label: 'accountNumber', value: 'id' }}
                placeholder="请选择银行账号"
              />
            </Form.Item>
            <Form.Item label="开户行" name="ourAccountBank">
              <Input placeholder="自动带出" disabled />
            </Form.Item>
            <Form.Item
              label="对方账户名"
              name="oppositeAccountName"
              initialValue={defaultOppositeAccountName}
              rules={[
                {
                  required: true,
                  message: '请选择对方账户名！',
                },
              ]}
            >
              <Input placeholder="请输入对方账户名" />
            </Form.Item>
            <Form.Item
              label="银行账号"
              name="oppositeAccountNumber"
              initialValue={defaultOppositeAccount}
              rules={[
                {
                  required: true,
                  message: '请输入银行账号！',
                },
              ]}
            >
              <Input placeholder="请输入银行账号" />
            </Form.Item>
            <Form.Item
              name="oppositeAccountBank"
              label="开户行"
              initialValue={defaultOppositeAccountBank}
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
        )
      }}
    </Modal>
  )
}
export default observer(PaymentModal)
