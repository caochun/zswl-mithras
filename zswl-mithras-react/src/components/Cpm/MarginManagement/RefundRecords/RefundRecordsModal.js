import { observer } from '@zswl/admin'
import { Button, DatePicker, Descriptions, Input, InputNumber, Upload } from 'antd'
import { Modal, Select, Form, App } from '@zswl/components'
import { UploadOutlined } from '@ant-design/icons'
import FormUpload from '@/components/FormUpload'
import { amountFormat, formateCard, getInputNumberAmountProps } from '@/utils'
import store from './store'
import Amount from '@/components/Amount'
import { useEffect, useState } from 'react'

//新增联系人弹窗
function Index() {
  const { addOreditRefund, ourBankInfoData } = store
  const [fileList, setFileList] = useState([])

  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])

  useEffect(() => {
    store.searchClient('', 1)
  }, [])
  useEffect(() => {
    form.setFieldsValue({
      ourBankAccountIdName: ourBankInfoData?.accountNumber,
      name1: ourBankInfoData?.accountBank,
    })
  }, [ourBankInfoData])
  const { clientList, backList, clientListOther, backListOther } = store
  const [form] = Form.useForm()

  const bankNumChange = (e, s, f) => {
    if (f == 1) {
      form.setFieldsValue({ name1: s.accountBank })
    } else {
      form.setFieldsValue({ name2: s.accountBank })
    }
  }
  const uploadProps = {
    name: 'file',
    beforeUpload: (file) => {
      setFileList([file])
      return false
    },
    onRemove: () => {
      setFileList([])
    },
    onPreview: (file) => {
      window.open(`/preview/reportPreview/${file.id}`)
    },
    fileList,
  }
  const getFile = (e) => {
    if (Array.isArray(e)) {
      return e
    }
    return e && e.fileList
  }

  let data = {}
  if (addOreditRefund == 2) {
    data.footer = null
  }
  return (
    <Modal
      title={'保证金退款'}
      store={store.refundModal}
      okText={'确定'}
      width={480}
      {...data}
      afterClose={store.refundModalClose}
      bodyStyle={{ maxHeight: '560px', overflowY: 'auto' }}
      destroyOnClose
    >
      <Form labelCol={{ span: 5 }} wrapperCol={{ span: 18 }} preserve={false} form={form}>
        <Form.Item name={'collectionType'} label="付款方式" initialValue={'保证金退款'}>
          <Input disabled />
        </Form.Item>
        <Form.Item
          name={'collectionAmount'}
          label="实付金额"
          rules={[
            {
              required: true,
              message: '请输入实付金额！',
            },
            Amount.rule,
          ]}
        >
          <Amount>
            <InputNumber style={{ width: '100%' }} disabled />
          </Amount>
        </Form.Item>
        <Form.Item
          name={'collectionDate'}
          label="实付日期"
          rules={[
            {
              required: true,
              message: '请选择实付日期！',
            },
          ]}
        >
          <DatePicker placeholder="请选择实付日期！" style={{ width: '100%' }} disabled />
        </Form.Item>

        {/* <Form.Item label="附言" name={'postscript'}>
          <Input.TextArea placeholder="请输入附言" />
        </Form.Item>
        <Form.Item label="附件" name="file">
          <FormUpload />
        </Form.Item> */}
        <Form.Item
          label="我方账户名"
          name={'ourBankAccountId'}
          rules={[
            {
              required: true,
              message: '请选择我方账户名！',
            },
          ]}
        >
          <Select
            disabled
            placeholder="请选择我方账户名"
            debounceSearch
            showSearch
            options={(e) => store.searchClient(e, 1)}
            fieldNames={{ label: 'accountName', value: 'id' }}
            onChange={(e, s) => {
              store.bankChanges(e, s, 1)
            }}
          />
        </Form.Item>
        <Form.Item
          name={'ourBankAccountIdName'}
          label="银行账号"
          rules={[
            {
              required: true,
              message: '请选择银行账号！',
            },
          ]}
        >
          <Select
            disabled
            placeholder="请选择银行账号"
            options={backList}
            fieldNames={{ label: 'accountNumber', value: 'id' }}
            onChange={(e, s) => {
              bankNumChange(e, s, 1)
            }}
          />
        </Form.Item>
        <Form.Item label="银行名称" name={'name1'}>
          <Input disabled placeholder="自动带出" />
        </Form.Item>
        <Form.Item
          label="对方账户名"
          name={'otherBankAccountId'}
          rules={[
            {
              required: true,
              message: '请输入对方账户名！',
            },
          ]}
        >
          <Input placeholder="请输入对方账户名！" disabled />

          {/* <Select
            placeholder="请选择对方账户名"
            debounceSearch
            showSearch
            fieldNames={{ label: 'clientName', value: 'id' }}
            options={(e) => store.searchClient(e, 2)}
            // onSearch={(e) => {

            // }}
            onChange={(e, s) => {
              store.bankChanges(e, s, 2)
            }}
          /> */}
        </Form.Item>
        <Form.Item
          name={'otherBankAccountIdName'}
          label="银行账号"
          getValueFromEvent={(e) => formateCard(e.target.value)}
          rules={[
            {
              required: true,
              message: '请输入银行账号！',
            },
          ]}
        >
          <Input placeholder="请输入银行账号！！" disabled />

          {/* <Select
            placeholder="请选择银行账号"
            options={backListOther}
            fieldNames={{ label: 'accountNumber', value: 'id' }}
            onChange={(e, s) => {
              bankNumChange(e, s, 2)
            }}
          /> */}
        </Form.Item>
        <Form.Item label="银行名称" name={'name2'}>
          <Input placeholder="请输入银行名称" disabled />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
