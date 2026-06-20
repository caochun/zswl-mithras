import { makeAutoObservable, observer } from '@zswl/admin'
import { Page, PageStore } from '@zswl/components'
import Api from '@/api/externalEmbed/bridgeAuthApi'
import JSEncrypt from 'jsencrypt'
import { useMemo } from 'react'

const pubKey =
  'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNpMKIVmt0u5lx62tRD1O/15EyNLN0lNi3++ytnvLalkQNSrrqU2w3uD5NwdVE/v4OrDznTpBdTl6N1ryXAILU5GDu0bLATC46RKxDlH52LIvaRBU7BZkEGqllEqRJFmwtvtNCVeZD6ekJWc67MLUh4LNa1yMQ9V6Zsf64uY2lgwIDAQAB'

const bridgeAuthInfo = {
  password: 'Zszl@2025',
  account: 'zszl',
}

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  token = null

  page = new PageStore({
    request: async () => {
      const res = await this.handleLogin()
      return res
    },
  })

  handleLogin = async () => {
    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(pubKey)
    const params = {
      account: bridgeAuthInfo.account,
      password: encrypt.encrypt(bridgeAuthInfo.password),
    }
    let tempRandom = null
    try {
      const res = await Api.getAuthCode(params)
      tempRandom = res.data
    } finally {
      const { csrfToken, tdToken } = await Api.login({ ...params, tempRandom })

      return { csrfToken, tdToken, tempRandom }
    }
  }
}

function paramsToString(params) {
  let search = []

  for (let key in params) {
    if (params.hasOwnProperty(key)) {
      let item = [key, encodeURIComponent(params[key])]
      search.push(item.join('='))
    }
  }

  return search.join('&')
}

const BridgeIframe = ({ frontend, path, query = {} }) => {
  const store = useMemo(() => new Store(), [])
  const { tdToken, csrfToken } = store.page.getData()
  const newQuery = {
    'td-token': tdToken,
    'x-cf-random': csrfToken,
    referer: 'http://gljsc.zjzsfh.com/handle/welcome',
    ...query,
  }
  const url = `${frontend}/${path}?${paramsToString(newQuery)}`

  return (
    <Page
      store={store}
      noStyle
      style={{
        width: '100%',
        height: '100%',
        backgroundColor: '#fff',
        padding: 0,
      }}
      bodyStyle={{
        width: '100%',
        height: '100%',
        padding: 0,
      }}
    >
      <iframe src={url} width="100%" height="100%" style={{ border: 'none' }}></iframe>
    </Page>
  )
}

export default observer(BridgeIframe)
