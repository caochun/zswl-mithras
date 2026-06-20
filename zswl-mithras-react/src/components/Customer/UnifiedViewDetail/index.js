import { Tabs, Card, Tag, Space, Button } from 'antd'
import styles from './style.less'
// 引入所有标签页组件
import Gl from './components/Gl/index'
import Kh from './components/Kh/index'
import Wb from './components/Wb/index'
// import Gs from './components/Gs/index'
// import Zx from './components/ZX/index'
// import Fl from './components/Fl/index'
// import Jy from './components/Jy/index'
// import Yq from './components/Yq/index'
// import Yj from './components/Yj/index'
// import GlGx from './components/GlGx/index'
import FinancialReport from './components/FinancialReport'
import { App, Page } from '@zswl/components'
import AreaData from './components/AreaData'
import store from './store'
import { history, observer } from '@zswl/admin'
import { useEffect, useLayoutEffect, useState } from 'react'
import BreadcrumbList from '@/layout/BreadcrumbList'
import { LeftOutlined } from '@ant-design/icons'

const CustomerDetail = ({ path, params: { id }, query, pathname }) => {
  const { enterpriseName, uscc } = query
  const pageDetail = store.page.getData()
  const detail = { ...store.detail, ...pageDetail }
  const isExternal = !!id
  // 顶部指标数据
  const [activeKey, setActiveKey] = useState(isExternal ? 'Gl' : 'Gs')

  const indicatorsList = [
    {
      value: detail?.overdueAmount,
      label: '逾期金额',
      key: 'overdueAmount',
      tabKey: 'Fl',
      dataIndex: 'overdueAmount',
      needHide: true,
    },
    {
      value: detail?.overdue ? 1 : -1,
      label: '票据逾期（逾期开始时间）',
      key: 'overdue',
      dataIndex: 'overdue',
      renderText: `${detail?.overdue ? `是（${detail?.overdueStartDate ?? '-'} ）` : '否'}`,
    },
    {
      value: detail?.legalAction,
      label: '法律诉讼',
      key: 'legalAction',
      tabKey: 'Fl',
      dataIndex: 'legalAction',
    },
    {
      value: detail?.bizRisk,
      label: '经营风险',
      key: 'creditRisk',
      tabKey: 'Jy',
      dataIndex: 'bizRisk',
    },
    {
      value: detail?.negativePo,
      label: '负面舆情',
      key: 'debt',
      tabKey: 'Yq',
      dataIndex: 'negativePo',
    },
  ]

  // 标签页配置
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
    // {
    //   key: 'Gs',
    //   label: '公司信息',
    //   children: <Gs id={id} />,
    // },
    // {
    //   key: 'GlGx',
    //   label: '关联关系',
    //   children: <GlGx />,
    // },
    // {
    //   key: 'Zx',
    //   label: '征信信息',
    //   children: <Zx id={id} />,
    // },
    // {
    //   key: 'Fl',
    //   label: '法律诉讼',
    //   children: <Fl id={id} />,
    // },
    // {
    //   key: 'Jy',
    //   label: '经营风险',
    //   children: <Jy id={id} />,
    // },
    // {
    //   key: 'Yq',
    //   label: '舆情',
    //   children: <Yq id={id} />,
    // },
    // {
    //   key: 'Yj',
    //   label: '预警',
    //   children: <Yj id={id} />,
    // },
    isExternal && {
      key: 'financialReport',
      label: '财务报表',
      children: <FinancialReport id={id} />,
    },
    // detail?.regionTab && {
    //   key: 'areaData',
    //   label: '区域数据',
    //   children: <AreaData id={id} />,
    // },
  ]

  const onChange = (key) => {
    setActiveKey(key)
  }

  const infoMap = {
    BLACK_LIST: {
      color: '#f1293f',
      text: '黑名单',
      icon: '/public/assets/risk/customerView/black.svg',
    },
    GRAY_LIST: {
      color: '#ff7738',
      text: '灰名单',
      icon: '/public/assets/risk/customerView/gray.svg',
    },
  }
  const currentInfo = infoMap[detail?.blackGrayType] ?? {
    color: '#3d6cde',
    text: '未命中',
    icon: '/public/assets/risk/customerView/white.svg',
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
        {/* <div className={styles['customer-detail-header']}> */}
          {/* 顶部企业基本信息 */}
          {/* <div className={styles['company-header']}>
            <div className={styles['company-desc']}>{detail?.bizScope ?? detail?.description}</div>
          </div> */}

          {/* 顶部指标卡片 */}
          {/* <div className={styles['indicator-cards']}>
            <div className={styles['indicator-cards-title']}>
              <img src={currentInfo.icon} alt="黑名单" style={{ width: 126, height: 126 }} />
            </div>
            {indicatorsList.map((indicator) => {
              if (indicator.needHide && !indicator.value) return null
              const isSpecial = indicator.key === 'overdue'
              const hasTabKey = !!indicator.tabKey
              return (
                <div
                  key={indicator.key}
                  className={styles['indicator-card']}
                  style={{
                    cursor: hasTabKey ? 'pointer' : 'default',
                  }}
                  onClick={() => {
                    hasTabKey && setActiveKey(indicator.tabKey)
                  }}
                >
                  <div
                    className={styles['indicator-item']}
                    style={{ color: indicator.value > 0 ? '#ff5d5d' : '#468ef8' }}
                  >
                    <div className={styles['indicator-value']}>
                      {!isSpecial ? indicator.value ?? '-' : indicator.renderText}
                    </div>
                    <div className={styles['indicator-label']}>{indicator.label}</div>
                  </div>
                </div>
              )
            })}
            <div className={styles['indicator-card']}>
              <div className={styles['indicator-item']}>
                <div style={{ fontSize: 20, fontWeight: 600, color: '#000' }}>
                  {detail?.dataYear ?? '-'}
                </div>
                <div className={styles['indicator-label']}>财报期数</div>
              </div>
            </div>
          </div> */}
        {/* </div> */}
        {/* 主要内容区域 */}
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
