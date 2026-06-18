import { Button, Form, Select } from '@zswl/components'
import { Col, Descriptions, Cascader, Input, Popconfirm, Row, Space, message } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import api from '../../api'
import { observer } from '@zswl/admin'
import { treeData } from './context'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 19 },
}
const Notice = ({ store }) => {
  const { preview, send, getNoticeDetail, noticeDetail, formStore, previewUrl, pageParams } = store

  const [ourBankList, setOurBankList] = useState()
  useEffect(() => {
    getNoticeDetail()
    getOurBankList()
  }, [])

  useEffect(() => {
    if (noticeDetail) {
      formStore.setFieldsValue({
        ...noticeDetail,
        ourAccountNumber: [
          noticeDetail.bankId,
          noticeDetail.accountNumber,
          noticeDetail.accountName,
        ].filter(Boolean),
      })
    }
  }, [noticeDetail])
  const getOurBankList = async () => {
    const res = await api.getClientBankList({})
    if (res) {
      console.log(treeData(res))
      setOurBankList(treeData(res))
    }
  }

  const disable = useMemo(() => {
    if (!noticeDetail) {
      return true
    }
    return (
      noticeDetail?.sendFlag || !noticeDetail?.timeAvaliableFlag || pageParams.state === 'OVERDUE'
    )
  }, [noticeDetail])

  const downEmail = async () => {
    const { ourAccountNumber } = store.formStore.getFieldsValue(true)
    // if (ourAccountNumber?.length === 0) {
    //   message.info('请选择银行账号')
    //   return
    // }
    const [accountBank, accountNumber, accountName] = ourAccountNumber || []

    await api.emailDown({
      collectionId: pageParams?.collectionId,
      accountName,
      accountBank: ourBankList.find((item) => item.value === accountBank)?.label,
      accountNumber,
    })
  }

  return (
    <>
      <Row style={{ width: 600 }}>
        <Col span={20}>
          <Form.Item label="发送时间">{noticeDetail?.sendTime || '-'}</Form.Item>
        </Col>
        <Col span={4} style={{ textAlign: 'right' }}>
          <Space>
            <Button onClick={() => preview()} type="primary">
              预览
            </Button>
            <Button type="primary" disabled>
              发送
            </Button>
            {/* <Popconfirm
              title="是否确定发送?"
              placement="topRight"
              onConfirm={() => send()}
              okText="确定"
              disabled={disable}
              cancelText="取消"
            >
              <Button type="primary" disabled={disable}>
                {noticeDetail?.sendFlag ? '已发送' : '发送'}
              </Button>
            </Popconfirm> */}
          </Space>
        </Col>
      </Row>
      <Form store={store.formStore} style={{ width: 600 }} {...layout} disabled={disable}>
        <Form.Item
          label="收件人"
          name="receiverMail"
          // rules={[
          //   {
          //     required: true,
          //     message: '',
          //   },
          //   validatorEmail,
          // ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="主题"
          name="title"
          // rules={[
          //   {
          //     required: true,
          //     message: '请输入主题！',
          //   },
          // ]}
        >
          <Input />
        </Form.Item>
        <Form.Item label="备注" name="comment">
          <Input.TextArea />
        </Form.Item>
        <Form.Item
          label="银行账号"
          name="ourAccountNumber"
          rules={[
            {
              required: true,
              message: '请选择',
            },
          ]}
        >
          <Cascader options={ourBankList} />
        </Form.Item>
        {/* <Form.Item label="邮件正文" name="ourAccountBank">
          <Button type="primary" onClick={() => preview()}>
            点击预览
          </Button>
        </Form.Item> */}
      </Form>
      <div style={{ display: 'flex', 'justify-content': 'space-between', marginBottom: 10 }}>
        <h1>邮件正文</h1>
        <Button type="primary" onClick={downEmail} access={'rentCollectionEmailDown'}>
          下载
        </Button>
      </div>
      {previewUrl && <iframe style={{ width: '100%', height: '400px' }} src={previewUrl}></iframe>}
    </>
  )
}
export default observer(Notice)
