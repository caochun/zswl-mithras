import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import store from './store'
import { Input, message, Tabs } from 'antd'
import { useEffect, useState } from 'react'
import Api from './api'
import JSEncrypt from 'jsencrypt'

const { Item } = Form
function Index({ open, callBack }) {
  const [form] = Form.useForm()
  const [passwordForm] = Form.useForm()
  const [flag, setFlag] = useState(1)

  const { getUserInfoData } = store
  useEffect(() => {
    const orgRolesName = getUserInfoData.jobsName?.map(({ orgName }) => orgName)
    let jobsName = getUserInfoData.jobsName?.map((v) => {
      return v.jobNames?.map((item) => item.jobName)
    })
    form.setFieldsValue({
      userName: getUserInfoData.userName,
      phone: getUserInfoData.phone,
      email: getUserInfoData.email,
      jobsName,
      orgRolesName,
    })
  }, [getUserInfoData, open])

  const handleCancel = () => {
    callBack && callBack()
  }
  const handleOk = async () => {
    await passwordForm.validateFields()
    const pubKey =
      'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNpMKIVmt0u5lx62tRD1O/15EyNLN0lNi3++ytnvLalkQNSrrqU2w3uD5NwdVE/v4OrDznTpBdTl6N1ryXAILU5GDu0bLATC46RKxDlH52LIvaRBU7BZkEGqllEqRJFmwtvtNCVeZD6ekJWc67MLUh4LNa1yMQ9V6Zsf64uY2lgwIDAQAB'

    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(pubKey)
    let data = {}
    const { oldPwd, newPwd, newPwdCopy } = passwordForm.getFieldValue()
    if (newPwd != newPwdCopy) {
      message.info('新密码和确认密码不一致，请重新输入！')
      return
    }
    data.oldPwd = encrypt.encrypt(oldPwd)
    data.newPwd = encrypt.encrypt(newPwd)
    data.newPwdCopy = encrypt.encrypt(newPwdCopy)
    await Api.saveChangePwd(data)
    message.success('修改密码成功')
    store.logout()
  }

  const onChange = (key) => {
    setFlag(key)
    console.log(key)
  }
  let data = {}
  if (flag == 1) {
    data.footer = null
  }
  return (
    <Modal
      title={'用户信息'}
      open={open}
      {...data}
      okText={'确定'}
      width={480}
      onCancel={handleCancel}
      onOk={handleOk}
      destroyOnClose
    >
      <Tabs
        defaultActiveKey="1"
        onChange={onChange}
        items={[
          {
            label: '个人中心',
            key: '1',
            children: (
              <Form labelCol={{ span: 6 }} preserve={false} form={form}>
                <Item
                  label={'姓名'}
                  name={'userName'}
                  rules={[
                    { required: true },
                    // {
                    //   pattern: /^ [\u4e00 - \u9fa5] | [a-zA-Z]$/,
                    //   message: '请输入客户名称，只能是中英文',
                    // },
                  ]}
                >
                  <Input placeholder={'请输入'} autoComplete="off" disabled />
                </Item>
                <Item
                  label={'职位'}
                  name={'jobsName'}
                  rules={[
                    { required: true },
                    // {
                    //   pattern: /^ [\u4e00 - \u9fa5] | [a-zA-Z]$/,
                    //   message: '请输入客户名称，只能是中英文',
                    // },
                  ]}
                >
                  <Input placeholder={'请输入'} autoComplete="off" disabled />
                </Item>
                <Item label={'部门'} name={'orgRolesName'} rules={[{ required: true }]}>
                  <Input placeholder={'请输入'} autoComplete="off" disabled />
                </Item>
                <Item
                  label={'联系方式'}
                  name={'phone'}
                  rules={[
                    { required: true },
                    // {
                    //   pattern: /^ [\u4e00 - \u9fa5] | [a-zA-Z]$/,
                    //   message: '请输入客户名称，只能是中英文',
                    // },
                  ]}
                >
                  <Input placeholder={'请输入'} autoComplete="off" disabled />
                </Item>
                <Item
                  label={'电子邮件'}
                  name={'email'}
                  rules={[
                    { required: true },
                    // {
                    //   pattern: /^ [\u4e00 - \u9fa5] | [a-zA-Z]$/,
                    //   message: '请输入客户名称，只能是中英文',
                    // },
                  ]}
                >
                  <Input placeholder={'请输入'} autoComplete="off" disabled />
                </Item>
              </Form>
            ),
          },
          {
            label: '修改密码',
            key: '2',
            children: (
              <Form labelCol={{ span: 6 }} preserve={false} form={passwordForm}>
                <Item
                  label={'当前密码'}
                  name={'oldPwd'}
                  rules={[
                    { required: true },
                    // {
                    //   pattern: /^ [\u4e00 - \u9fa5] | [a-zA-Z]$/,
                    //   message: '请输入客户名称，只能是中英文',
                    // },
                  ]}
                >
                  <Input.Password placeholder="请输入" />
                </Item>
                <Item
                  label={'新密码'}
                  name={'newPwd'}
                  rules={[
                    { required: true },
                    // {
                    //   pattern: /^ [\u4e00 - \u9fa5] | [a-zA-Z]$/,
                    //   message: '请输入客户名称，只能是中英文',
                    // },
                  ]}
                >
                  <Input.Password placeholder="请输入" />
                </Item>
                <Item
                  label={'确认新密码'}
                  name={'newPwdCopy'}
                  rules={[
                    { required: true },
                    // {
                    //   pattern: /^ [\u4e00 - \u9fa5] | [a-zA-Z]$/,
                    //   message: '请输入客户名称，只能是中英文',
                    // },
                  ]}
                >
                  <Input.Password placeholder="请输入" />
                </Item>
              </Form>
            ),
          },
        ]}
      />
    </Modal>
  )
}

export default observer(Index)
