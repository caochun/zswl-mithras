import { observer, toJS } from '@zswl/admin'
import FormUpload from '@/components/FormUpload'
import { Button, DatePicker, Descriptions, Input, InputNumber, Upload } from 'antd'
import { Modal, Select, Form, App } from '@zswl/components'
import { UploadOutlined } from '@ant-design/icons'
import Amount from '@/components/Amount'
import { amountFormat, getInputNumberAmountProps } from '@/utils'
import { useState, useEffect } from 'react'
import store from './store'

//新增联系人弹窗
function Index() {
  const { addOredit, currentBankInfo } = store
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  const [form] = Form.useForm()
  const [fileList, setFileList] = useState([])
  const { clientList, backList } = store

  useEffect(() => {
    store.searchClient()
  }, [])

  useEffect(() => {
    form.setFieldsValue({
      bankAccountId: currentBankInfo?.accountNumber,
      bankname: currentBankInfo?.accountBank,
    })
  }, [currentBankInfo])
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
      // console.log(file, 'file2222')
      window.open(`/preview/reportPreview/${file.id}`)
    },
    fileList,
  }
  // const getFile = (e) => {
  //   if (Array.isArray(e)) {
  //     return e
  //   }
  //   return e && e.fileList
  // }
  const bankNumChange = (e, s) => {
    form.setFieldsValue({ bankname: s.accountBank })
  }
  let data = {}
  if (addOredit == 2) {
    data.footer = null
  }
  return (
    <Modal
      title={'收款记录'}
      store={store.collectionModal}
      okText={'确定'}
      width={480}
      {...data}
      afterClose={store.payModalClose}
      destroyOnClose
    >
      <Form labelCol={{ span: 5 }} wrapperCol={{ span: 18 }} preserve={false} form={form}>
        <Form.Item
          name={'collectionType'}
          label="收款类型"
          rules={[
            {
              required: true,
              message: '请选择收款类型！',
            },
          ]}
        >
          <Select placeholder="请选择收款类型！" options="paymentMethod" disabled />
        </Form.Item>
        <Form.Item
          name={'collectionDate'}
          label="实收日期"
          rules={[
            {
              required: true,
              message: '请选择实收日期！',
            },
          ]}
        >
          <DatePicker placeholder="请选择实收日期！" style={{ width: '100%' }} disabled />
        </Form.Item>
        <Form.Item
          name={'collectionAmount'}
          label="实收金额"
          rules={[
            {
              required: true,
              message: '请输入实收金额！',
            },
            Amount.rule,
          ]}
        >
          <Amount>
            <InputNumber style={{ width: '100%' }} disabled />
          </Amount>
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
              message: '请选择方账户名！',
            },
          ]}
        >
          <Select
            disabled
            placeholder="请选择我方账户名"
            debounceSearch
            showSearch
            options={(e) => store.searchClient(e)}
            fieldNames={{ label: 'accountName', value: 'id' }}
            // onSearch={(e) => {
            //   store.searchClient(e)
            // }}
            onChange={(e, s) => {
              store.bankChange(e, s)
            }}
          />
        </Form.Item>
        <Form.Item
          name={'bankAccountId'}
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
            fieldNames={{ label: 'accountNumber', value: 'id' }}
            options={backList}
            onChange={(e, s) => {
              bankNumChange(e, s)
            }}
            // onSearch={(e) => {
            //   store.searchBackNum(e)
            // }}
          />
        </Form.Item>
        <Form.Item label="银行名称" name={'bankname'}>
          <Input disabled placeholder="自动带出" />
        </Form.Item>
        {/* <Form.Item label="支行名称">
          <Input disabled placeholder="自动带出" />
        </Form.Item> */}
      </Form>
    </Modal>
  )
}

export default observer(Index)
