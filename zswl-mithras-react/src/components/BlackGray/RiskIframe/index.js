import { makeAutoObservable, observer } from '@zswl/admin'
import { Page, PageStore } from '@zswl/components'
import Api from '@/api/blackGray/queryExternalDataApi'
import JSEncrypt from 'jsencrypt'
import { useEffect, useMemo, useState } from 'react'

export const pubKey =
  'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNpMKIVmt0u5lx62tRD1O/15EyNLN0lNi3++ytnvLalkQNSrrqU2w3uD5NwdVE/v4OrDznTpBdTl6N1ryXAILU5GDu0bLATC46RKxDlH52LIvaRBU7BZkEGqllEqRJFmwtvtNCVeZD6ekJWc67MLUh4LNa1yMQ9V6Zsf64uY2lgwIDAQAB'

const infoMap = {
  prod: {
    jkUrl: 'http://zhfk.zjzsfh.com',
    jkSeverUrl: 'http://zhfk.zjzsfh.com/gungnirApi',
    password: 'Zswl@2024',
    account: 'zszl',
  },
  pre: {
    jkUrl: 'http://pre-zhfk.zjzsfh.com',
    jkSeverUrl: 'http://pre-zhfk.zjzsfh.com/gungnirApi',
    password: 'Zszl@2024',
    account: 'zszl',
  },
  // dev: {
  //   jkUrl: ' http://localhost:3001',
  //   jkSeverUrl: 'http://10.42.200.200/api',
  //   password: 'Wl@123456',
  //   account: 'admin',
  // },
}
const { jkUrl, jkSeverUrl, password, account } = infoMap[__ENV__] ?? infoMap.pre

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
      const { _salt_: salt } = await Api.getRiskIframeAuthCode(jkSeverUrl, params)
      _salt_ = salt
    } finally {
      const { csrfToken, _qjt_ac_ } = await Api.loginRiskIframe(
        jkSeverUrl,
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
const BlackGrayRiskIframe = ({ path, query }) => {
  const store = useMemo(() => new Store(), [])
  const { csrfToken, _salt_, _qjt_ac_ } = store.page.getData()
  const newQuery = {
    leaseToken: csrfToken,
    _salt_,
    _qjt_ac_,
    layout: 0,
    fianceName: 'zheShangZuLinSingle',
    ...query,
  }
  const url = `${jkUrl}/${path}?${paramsToString(newQuery)}`
  return (
    <Page
      store={store}
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
      <iframe src={url} width={'100%'} height={'100%'} style={{ border: 'none' }}></iframe>
    </Page>
  )
}

export default observer(BlackGrayRiskIframe)
