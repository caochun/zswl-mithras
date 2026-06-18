import { App, Select, Button, Form } from '@zswl/components'
import { observer, http } from '@zswl/admin'
import { Space, Input, message } from 'antd'
import JSEncrypt from 'jsencrypt'
import store from './store'
import Api from '@/api/layout/fastLoginApi'
import { setQjtAc, setSalt } from '@/utils'
const pubKey =
  'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNpMKIVmt0u5lx62tRD1O/15EyNLN0lNi3++ytnvLalkQNSrrqU2w3uD5NwdVE/v4OrDznTpBdTl6N1ryXAILU5GDu0bLATC46RKxDlH52LIvaRBU7BZkEGqllEqRJFmwtvtNCVeZD6ekJWc67MLUh4LNa1yMQ9V6Zsf64uY2lgwIDAQAB'

const { Item } = Form
function Index() {
  if (['uat', 'prod'].includes(__ENV__)) {
    return null
  }
  return <Content />
}
function Content() {
  const { getUserInfoData: user } = store
  const [form] = Form.useForm()
  const getList = async () => {
    const { userList } = await http.get('/user/list', {
      params: {
        curPage: 1,
        pageSize: 500,
      },
    })
    return userList
  }
  const login = async () => {
    const { account, password } = form.getFieldsValue()
    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(pubKey)
    const params = { account, password: encrypt.encrypt(password) }
    try {
      const { _salt_ } = await Api.getAuthCode(params)
      setSalt(_salt_)
    } finally {
      const { csrfToken, _qjt_ac_ } = await Api.login(params)
      setQjtAc(_qjt_ac_)
      App.setToken(csrfToken)
      message.success('切换成功')
      window.history.replaceState(null, '', '/')
      window.location.reload()
    }
  }
  return (
    <Form
      form={form}
      component={false}
      autoComplete="false"
      initialValues={{ password: '1', account: user.account }}
    >
      <Space style={{ marginRight: 20 }}>
        <Item noStyle name="account">
          <Select
            style={{ width: 120 }}
            getPopupContainer={() => document.body}
            options={getList}
            fieldNames={{ label: 'userName', value: 'account' }}
          />
        </Item>
        <Item noStyle name="password">
          <Input.Password
            style={{ width: 160 }}
            addonAfter={
              <Button size="small" type="link" onClick={login}>
                切换
              </Button>
            }
          />
        </Item>
      </Space>
    </Form>
  )
}

export default observer(Index)
