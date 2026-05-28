import styles from './style.less'
import Api from './api'
import { App, Button } from '@zswl/components'
import { Input, Form, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { setSalt, setQjtAc } from '@/utils'
import JSEncrypt from 'jsencrypt'
import { getQuery } from '@zswl/admin'

const { Item } = Form
const pubKey =
  'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNpMKIVmt0u5lx62tRD1O/15EyNLN0lNi3++ytnvLalkQNSrrqU2w3uD5NwdVE/v4OrDznTpBdTl6N1ryXAILU5GDu0bLATC46RKxDlH52LIvaRBU7BZkEGqllEqRJFmwtvtNCVeZD6ekJWc67MLUh4LNa1yMQ9V6Zsf64uY2lgwIDAQAB'

function Login() {
  const [form] = Form.useForm()
  const submit = async () => {
    const values = await form.validateFields()
    let { password, account } = values
    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(pubKey)
    password = encrypt.encrypt(password)
    const params = { account, password }
    try {
      const { _salt_ } = await Api.getAuthCode(params)
      setSalt(_salt_)
      const { csrfToken, _qjt_ac_, needResetPwd } = await Api.login(params)
      setQjtAc(_qjt_ac_)
      App.setToken(csrfToken)
      if (needResetPwd) {
        message.warning('初次登录,请修改密码!')
        setTimeout(() => {
          window.location.href = '/login?isChangePwd=true'
        }, 300)
      } else {
        window.location.href = '/'
      }
    } finally {
      //console.log('111')
    }
  }
  const isChangePwd = getQuery('isChangePwd') === 'true'
  const handleChange = async () => {
    const values = await form.validateFields()

    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(pubKey)
    let data = {}
    const { oldPwd, newPwd, newPwdCopy } = values
    if (newPwd != newPwdCopy) {
      message.info('新密码和确认密码不一致，请重新输入！')
      return
    }
    data.oldPwd = encrypt.encrypt(oldPwd)
    data.newPwd = encrypt.encrypt(newPwd)
    data.newPwdCopy = encrypt.encrypt(newPwdCopy)
    const { csrfToken, _qjt_ac_ } = await Api.saveChangePwd(data)
    setQjtAc(_qjt_ac_)
    App.setToken(csrfToken)
    message.success('修改密码成功')
    window.location.href = '/'
  }
  //回车登录
  document.onkeydown = function (e) {
    // 回车提交表单
    // 兼容FF和IE和Opera
    let theEvent = window.event || e
    let code = theEvent.keyCode || theEvent.which || theEvent.charCode
    if (code == 13) {
      submit()
    }
  }

  const onPasswordChange = (value) => {
    form.setFieldValue('password', value || '')
  }

  const LoginItem = () => {
    if (isChangePwd) {
      return (
        <>
          <Item
            label={'当前密码'}
            name={'oldPwd'}
            rules={[
              { required: true },
              { pattern: /(^\S)((.)*\S)?(\S*$)/, message: '前后不能有空格' },
            ]}
          >
            <Input.Password placeholder="请输入" autoComplete="off" />
          </Item>
          <Item
            label={'新密码'}
            name={'newPwd'}
            rules={[
              { required: true },
              { pattern: /(^\S)((.)*\S)?(\S*$)/, message: '前后不能有空格' },
            ]}
          >
            <Input.Password placeholder="请输入" autoComplete="off" />
          </Item>
          <Item
            label={'确认新密码'}
            name={'newPwdCopy'}
            rules={[
              { required: true },
              { pattern: /(^\S)((.)*\S)?(\S*$)/, message: '前后不能有空格' },
            ]}
          >
            <Input.Password placeholder="请输入" autoComplete="off" />
          </Item>
          <Item noStyle label=" " colon={false}>
            <Button block type="primary" onClick={handleChange}>
              确认修改
            </Button>
          </Item>
        </>
      )
    }

    return (
      <>
        <Item
          label="用户名"
          required={false}
          name="account"
          rules={[
            { required: true },
            { pattern: /(^\S)((.)*\S)?(\S*$)/, message: '前后不能有空格' },
          ]}
        >
          <Input prefix={<UserOutlined />} autoComplete="off" />
        </Item>
        <Item
          label="密码"
          required={false}
          name="password"
          rules={[
            { required: true },
            { pattern: /(^\S)((.)*\S)?(\S*$)/, message: '前后不能有空格' },
          ]}
        >
          <Input.Password prefix={<LockOutlined />} />
        </Item>
        <Item noStyle label=" " colon={false}>
          <Button block type="primary" onClick={submit} className={styles.loginBtn} id="submit">
            登录
          </Button>
        </Item>
      </>
    )
  }
  return (
    <div className={styles.loginPage}>
      <div className={styles.form}>
        <Form
          form={form}
          className={styles.login}
          labelCol={{ span: 12 }}
          size={'large'}
          layout={'vertical'}
          autoComplete="off"
        >
          <div className={styles.title}>浙商租赁核心业务系统</div>
          {LoginItem()}
        </Form>
      </div>
    </div>
  )
}

export default Login
