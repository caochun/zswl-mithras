import { http, configure, defineApp } from '@zswl/admin'
import { ConfigProvider, message } from 'antd'
import zhCN from 'antd/lib/locale/zh_CN'
import moment from 'moment'
import { App, Modal } from '@zswl/components'
import {
  getSalt,
  getQjtAc,
  handleHttpError,
  getUrlParam,
  setSalt,
  setQjtAc,
  baseURL,
  getMenuIdByPathname,
  getRandomString,
} from '@/utils'
import 'moment/locale/zh-cn'
import './app.less'
import { useEffect } from 'react'

message.config({
  duration: 2,
  maxCount: 2,
})

moment.locale('zh-cn')
configure({ enforceActions: 'never' })
http.setConfig({
  baseURL: baseURL(),
  mock: { yapiUrl: 'http://yapi.zswltec.com:3000/mock/11' },
  headers(config) {
    const baseHeaders = {
      token: JSON.stringify({
        _salt_: getSalt(),
        _qjt_ac_: getQjtAc(),
      }),
      'X-Cf-Random': App.getToken(),
      source: 'PC',
      menuCode: getMenuIdByPathname(),
      'X-Request-Id': getRandomString(),
    }
    return baseHeaders
  },
  transformResult(res) {
    const { success, data, msg, toast } = res.data
    toast && message.info(toast)
    if (success) {
      return data
    }
    handleHttpError(res.data)
    return Promise.reject(msg)
  },
  error: async (e) => {
    const { data } = e.response || {}
    handleHttpError(data)
  },
})
function Application({ children }) {
  const ticket = getUrlParam('ticket')
  const service = getUrlParam('service')
  const token = getUrlParam('token')

  useEffect(() => {
    service && init()
  }, [ticket, service, token])

  useEffect(() => {
    // getGrayStatus()
  }, [])

  const init = async () => {
    const res = await http.post('/user/portal/ssoLogin', { ticket, service })
    if (res) {
      const { csrfToken, _qjt_ac_, _salt_, url } = res
      setSalt(_salt_)
      setQjtAc(_qjt_ac_)
      App.setToken(csrfToken)
      window.location.href = url
    }
  }
  const getGrayStatus = async () => {
    const res = await http.get('/system/switch/memorialDay')

    if (res) {
      document.documentElement.className = 'gray'
    }
  }

  return (
    <ConfigProvider
      locale={zhCN}
      autoInsertSpaceInButton={false}
      getPopupContainer={App.getPopupContainer}
      getTargetContainer={App.getTargetContainer}
    >
      {children}
    </ConfigProvider>
  )
}
// export default Application

export default defineApp(Application, {
  keepAlive: (params) => {
    console.log('params: ', params)
    const noCache = ['/budgetManagement/provisionForecast']
    return !!App.getToken()

    // return !!App.getToken() && ['preSvc', 'prod', 'pre_new', 'prod_new'].includes(__ENV__)
  },
})
