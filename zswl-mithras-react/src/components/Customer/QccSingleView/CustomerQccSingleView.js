import { Page } from '@zswl/components'
import { useState, useEffect } from 'react'
import CustomerViewApi from '@/api/customerView/customerDetailApi'
import BreadcrumbList from '@/layout/BreadcrumbList'

const CustomerQccSingleView = ({ query }) => {
  const { customerName } = query
  const [url, setUrl] = useState('')

  useEffect(() => {
    async function loadUrl() {
      const res = await CustomerViewApi.generateToken(null)
      const returnUrl = `/company-gateway?keyword=${customerName}`
      const url = `https://pro-plugin.qcc.com/plugin-login?key=${encodeURIComponent(
        res.companyKey
      )}&token=${encodeURIComponent(res.token)}&returnUrl=${returnUrl}`
      setUrl(url)
    }

    loadUrl()
  }, [customerName])

  if (!url) {
    return null
  }

  return (
    <Page
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
      <BreadcrumbList />
      <iframe src={url} height={'100%'} width={'100%'} style={{ border: 'none' }}></iframe>
    </Page>
  )
}

export default CustomerQccSingleView
