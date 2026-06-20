import FormUpload from '@/components/FormUpload'
import { getInputNumberAmountProps, validatorAmount } from '@/utils'
import { App, Form, Modal } from '@zswl/components'
import { Button, DatePicker, Input, InputNumber, Select, Upload } from 'antd'
import { useEffect, useState } from 'react'
import api from '@/api/cpm/collectionWriteOffApi'

const noList = ['服务费/咨询费/手续费', '首期租金', '名义价款']
const CollectionModal = ({ activeData, store }) => {
  const [ourBankList, setOurBankList] = useState()
  const [form] = Form.useForm()
  const storeData = store.page.getData()
  const isRequire = !noList.find((item) => storeData.cashFlowItem === item)

  const layout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 19 },
  }
  useEffect(() => {
    getOurBankList()
  }, [])
  const getOurBankList = async () => {
    const res = await api.getClientBankList({})
    if (res) {
      setOurBankList(res)
    }
  }
  const bankChange = (val, list) => {
    const data = list.find((item) => item.id === val.value)
    if (data) {
      const { accountBank, accountNumber } = data

      form.setFieldsValue({
        ourAccountNumber: accountNumber,
        ourAccountBank: accountBank,
      })
    }
  }
  return (
    <Modal
      propsBy={(data) => {
        return {
          title: (!data ? '新增' : data?.noEdit ? '' : '编辑') + '收款明细',
          footer: data?.noEdit ? <></> : undefined,
        }
      }}
      store={store.collectionModal}
      destroyOnClose
    >
      {(data) => {
        return (
          <Form form={form} {...layout} disabled={data?.noEdit}>
            <Form.Item
              label="收款类型"
              name="collectionType"
              rules={[
                {
                  required: true,
                  message: '请选择收款类型！',
                },
              ]}
            >
              <Select
                options={App.getData().optionsType.paymentMethod}
                placeholder="请选择收款类型！"
              />
            </Form.Item>
            <Form.Item
              label="实付日期"
              name="collectionDate"
              rules={[
                {
                  required: true,
                  message: '请选择实收日期！',
                },
              ]}
            >
              <DatePicker placeholder="请选择实收日期！" />
            </Form.Item>
            <Form.Item
              label="实收金额(元)"
              name="collectionAmount"
              rules={[
                {
                  required: true,
                  message: '请输入实收金额！',
                },
                validatorAmount,
              ]}
            >
              <InputNumber
                style={{ width: '100%' }}
                {...getInputNumberAmountProps()}
                placeholder="请输入实收金额！"
              />
            </Form.Item>
            <Form.Item
              label="本金(元)"
              name="principal"
              rules={[
                {
                  required: isRequire,
                  message: '请输入本金！',
                },

                validatorAmount,
              ]}
            >
              <InputNumber
                disabled={!isRequire || data?.noEdit}
                style={{ width: '100%' }}
                {...getInputNumberAmountProps()}
                placeholder="请输入本金！"
              />
            </Form.Item>
            <Form.Item
              label="利息(元)"
              name="interest"
              rules={[
                {
                  required: isRequire,
                  message: '请输入利息！',
                },
                validatorAmount,
              ]}
            >
              <InputNumber
                disabled={!isRequire || data?.noEdit}
                style={{ width: '100%' }}
                {...getInputNumberAmountProps()}
                placeholder="请输入利息！"
              />
            </Form.Item>
            <Form.Item
              label="罚息(元)"
              required={isRequire}
              name="penaltyInterest"
              rules={[
                {
                  required: isRequire,
                  message: '请输入罚息！',
                },
                validatorAmount,
              ]}
            >
              <InputNumber
                disabled={!isRequire || data?.noEdit}
                style={{ width: '100%' }}
                {...getInputNumberAmountProps()}
                placeholder="请输入罚息！"
              />
            </Form.Item>

            {/* <Form.Item name="postscript" label="附言">
              <Input.TextArea placeholder="请输入附言" />
            </Form.Item>
            <Form.Item name="file" label="附件">
              <FormUpload />
            </Form.Item> */}
            <Form.Item label="是否开票" name="invoice">
              <Input disabled placeholder=" " />
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
                onChange={(val) => bankChange(val, ourBankList)}
                options={ourBankList}
                labelInValue
                fieldNames={{ label: 'accountName', value: 'id' }}
                placeholder="请选择银行账号"
              />
            </Form.Item>
            <Form.Item label="银行账号" name="ourAccountNumber">
              <Input disabled placeholder="自动带出" />
            </Form.Item>
            <Form.Item label="开户行" name="ourAccountBank">
              <Input disabled placeholder="自动带出" />
            </Form.Item>
          </Form>
        )
      }}
    </Modal>
  )
}
export default CollectionModal
