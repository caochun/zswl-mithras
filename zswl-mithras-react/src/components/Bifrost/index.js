import { useEffect, useRef } from 'react'
import styles from './style.less'
import { App } from '@zswl/components'
import { getQjtAc, getSalt } from '@/utils'
import { Alert } from 'antd'
// import { localIp } from '@zswl/admin'

function Index({ path }) {
  const ref = useRef()
  let src = `/bifrost${path}`
  if (process.env.NODE_ENV === 'development') {
    // src = `http://10.158.250.87:8800/bifrost${path}`
    // src = `http://localhost:8800/bifrost${path}`
    return (
      <Alert
        message="本地开发环境不支持嵌入bifrost项目，请前往测试环境操作。"
        type="info"
        showIcon
      />
    )
  }
  const ready = (ev) => {
    const { type } = ev.data
    if (type === 'bifrost-ready') {
      ref.current.contentWindow.postMessage(
        {
          type: 'setLoginInfo',
          data: {
            salt: getSalt(),
            token: App.getToken(),
            qjt_ac: getQjtAc(),
          },
        },
        '*'
      )
    }
  }
  useEffect(() => {
    window.addEventListener('message', ready)
    return () => {
      window.removeEventListener('message', ready)
    }
  }, [])
  return <iframe className={styles.iframe} ref={ref} src={src} />
}

export default Index
