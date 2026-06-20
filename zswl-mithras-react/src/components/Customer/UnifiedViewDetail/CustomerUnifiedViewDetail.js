import { Tabs, Card, Tag, Space } from 'antd'
import styles from './style.less'
import Gl from './components/Gl'
import Kh from './components/Kh'
import Wb from './components/Wb'
import FinancialReport from './components/FinancialReport'
import { App, Page } from '@zswl/components'
import store from './store'
import { history, observer } from '@zswl/admin'
import { useState } from 'react'
import BreadcrumbList from '@/layout/BreadcrumbList'
import { LeftOutlined } from '@ant-design/icons'

const CustomerDetail = ({ path, params: { id }, query, pathname }) => {
  const { enterpriseName, uscc } = query
  const pageDetail = store.page.getData()
  const detail = { ...store.detail, ...pageDetail }
  const isExternal = !!id
  const [activeKey, setActiveKey] = useState(isExternal ? 'Gl' : 'Gs')

  const tabItems = [
    isExternal && {
      key: 'Gl',
      label: '概览',
      children: <Gl id={id} />,
    },
    {
      key:'Wb',
      label:'外部信息',
      children: <Wb id={id}/>
    },
    {
      key:'Kh',
      label:'客户风险',
      children: <Kh id={id} enterpriseName={enterpriseName}/>
    },
    isExternal && {
      key: 'financialReport',
      label: '财务报表',
      children: <FinancialReport id={id} />,
    },
  ]

  const onChange = (key) => {
    setActiveKey(key)
  }

  return (
    <Page
      params={{ id, enterpriseName, uscc }}
      store={store}
      style={{ padding: 0 }}
      className={styles['customer-page']}
    >
      <div className={styles['company-name']}>
        <BreadcrumbList pathname={pathname} />
        <Space className={styles['title']}>
          <LeftOutlined onClick={() => history.goBack()} />
          {enterpriseName}
          {!!detail?.clientStatus && (
            <Tag color="green">{App.matchOption('clientStatus', detail.clientStatus)?.label}</Tag>
          )}
          {detail?.belongDeptName && <Tag color="#a274fa">{detail?.belongDeptName}</Tag>}
          {/* <Tag
            color="#1e69f7"
            style={{ cursor: 'pointer' }}
            onClick={() => history.push(`/customerView/singleView?customerName=${enterpriseName}`)}
          >
            客户风险单一视图
          </Tag> */}
          <Tag
            color="#1e69f7"
            style={{ cursor: 'pointer' }}
            onClick={() => {
              history.push(`/customerView/singleView?customerName=${uscc||enterpriseName}`)
            }}
          >
            企查查
          </Tag>
        </Space>
      </div>
      <div className={styles['customer-detail']}>
        <Card bordered={false} className={styles['main-content']}>
          <Tabs
            defaultActiveKey="overview"
            items={tabItems}
            onChange={onChange}
            activeKey={activeKey}
            type="line"
            size="large"
            animated={{ tabPane: true }}
            style={{width: activeKey === 'Kh' ? '100vw' : 'auto'}}
          />
        </Card>
      </div>
    </Page>
  )
}

export default observer(CustomerDetail)
