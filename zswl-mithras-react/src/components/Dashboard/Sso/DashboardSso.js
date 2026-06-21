import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { App } from '@zswl/components'
import { setSalt, setQjtAc } from '@/utils'
import { message } from 'antd'
import styles from './index.less'
import ssoApi from '@/api/dashboard/ssoApi'

function DashboardSso({ query: { ticket = '' } }) {
  useEffect(() => {
    if (!ticket) {
      //   window.location.href = `http://10.10.48.50:8088/ssoserver/caslogin/agentlogin?service=${window.location.href}`
      message.error('缺少ticket参数')
    } else {
      ssoApi
        .postDashboardSsoLogin({
          ticket,
        })
        .then((res) => {
          const { csrfToken, _qjt_ac_, _salt_, url } = res
          setSalt(_salt_)
          setQjtAc(_qjt_ac_)
          App.setToken(csrfToken)
          window.location.href = url
        })
    }
  }, [ticket])
  return (
    <div className={styles.ssoLoginBox}>
      <span>登录中，请稍等...</span>
    </div>
  )
}

export default observer(DashboardSso)
