import { Page } from '@zswl/components'
import { useState, useEffect } from 'react'
import { http } from '@zswl/admin'
import BreadcrumbList from '@/layout/BreadcrumbList'

const Index = ({ query }) => {
    const { customerName, pathname } = query
    const [url, setUrl] = useState('')
    useEffect(async() => {
        const res = await http.post('/qccApi/generateToken', null, {
            mock:false,
        })
        const returnUrl = `/company-gateway?keyword=${customerName}`
        const url = `https://pro-plugin.qcc.com/plugin-login?key=${encodeURIComponent(res.companyKey)}&token=${encodeURIComponent(res.token)}&returnUrl=${(returnUrl)}`
        setUrl(url)
    }, [])
    if(!url){
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
            <BreadcrumbList/>
            <iframe src={url} height={'100%'} width={'100%'} style={{ border: 'none' }}></iframe>
        </Page>
    )
}

export default Index
