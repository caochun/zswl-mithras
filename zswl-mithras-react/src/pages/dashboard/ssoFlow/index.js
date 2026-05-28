import { useEffect } from 'react'
import { http, observer } from '@zswl/admin'
import { App } from '@zswl/components'
import { setQjtAc } from '@/utils'
import qs from 'query-string'
import styles from './index.less'

function Index({ query: { params = '{}', cbUrl = '/' } }) {
  const paramsObj = JSON.parse(params)

  useEffect(() => {
    const { secret, timestamp, random, mithrasClientId, userid, loginid, workcode } = paramsObj
    http
      .post(
        '/message/oa/auth',
        {
          secret,
          timestamp,
          random,
          mithrasClientId,
        },
        {
          headers: {
            token: JSON.stringify({
              userid,
              loginid,
              workcode,
              timestamp,
              random,
              secret,
              device: 'PC',
            }),
          },
        }
      )
      .then((res) => {
        if (res) {
          App.setToken(res.csrfToken)
          setQjtAc(res._qjt_ac_)
          const {
            secret,
            timestamp,
            random,
            mithrasClientId,
            mithrasMsgId,
            userid,
            loginid,
            workcode,
            ...rest
          } = paramsObj
          window.location.href = `${cbUrl}?${qs.stringify(rest)}`
        }
      })
  }, [params, cbUrl])
  return (
    <div className={styles.ssoLoginBox}>
      <span>登录中，请稍等...</span>
    </div>
  )
}

export default observer(Index)
