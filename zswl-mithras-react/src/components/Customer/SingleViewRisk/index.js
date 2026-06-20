import { makeAutoObservable, observer } from '@zswl/admin'
import { Page, PageStore } from '@zswl/components'
import Api from '@/api/customer/externalPublicInfoApi'
import JSEncrypt from 'jsencrypt'
import { useMemo } from 'react'
import BreadcrumbList from '@/layout/BreadcrumbList'

export const pubKey =
  'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNpMKIVmt0u5lx62tRD1O/15EyNLN0lNi3++ytnvLalkQNSrrqU2w3uD5NwdVE/v4OrDznTpBdTl6N1ryXAILU5GDu0bLATC46RKxDlH52LIvaRBU7BZkEGqllEqRJFmwtvtNCVeZD6ekJWc67MLUh4LNa1yMQ9V6Zsf64uY2lgwIDAQAB'

const infoMap = {
  prod: {
    jkUrl: 'http://10.158.4.101',
    password: 'Zswl@2024',
    account: 'zszl',
  },
  // preSvc: {
  //   jkUrl: 'http://10.158.4.101',
  //   password: 'Zswl@2024',
  //   account: 'zszl',
  // },
  preSvc: {
    jkUrl: 'http://pre-zhfk.zjzsfh.com',
    password: 'Wl@123456',
    account: 'admin',
  },
  sit: {
    jkUrl: 'http://pre-zhfk.zjzsfh.com',
    password: 'Wl@123456',
    account: 'admin',
  },
}
const { jkUrl, password, account } = infoMap[__ENV__] ?? infoMap.prod

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
    const params = { account, password: encrypt.encrypt(password) }
    let _salt_ = null
    try {
      const { _salt_: salt } = await Api.getSingleViewRiskAuthCode(params)
      _salt_ = salt
    } finally {
      const { csrfToken, _qjt_ac_ } = await Api.loginSingleViewRisk(
        params,
        JSON.stringify({ _salt_, _qjt_ac_: null })
      )

      return { csrfToken, _salt_, _qjt_ac_ }
    }
  }
}

function paramsToString(params) {
  let search = []

  for (let key in params) {
    if (params.hasOwnProperty(key)) {
      let item = [key, params[key]]
      search.push(item.join('='))
    }
  }

  return search.join('&')
}
const Index = ({ query, pathname }) => {
  const store = useMemo(() => new Store(), [])
  const { csrfToken, _salt_, _qjt_ac_ } = store.page.getData()
  const { customerName, showBreadcrumb } = query
  const newQuery = {
    leaseToken: csrfToken,
    _salt_,
    _qjt_ac_,
    layout: 0,
    fianceName: 'zheShangZuLinSingle',
    customerName,
  }
  const url = `${jkUrl}/singleView/lease?${paramsToString(newQuery)}`
  return (
    <Page
      store={store}
      noStyle
      style={{
        width: '100%',
        height: '100%',
      }}
      bodyStyle={{
        width: '100%',
        height: '100%',
      }}
    >
      {showBreadcrumb && <BreadcrumbList />}
      <iframe src={url} width={'100%'} height={'100%'} style={{ border: 'none' }}></iframe>
    </Page>
  )
}

export default observer(Index)
