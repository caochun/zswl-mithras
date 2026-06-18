import { App, AppLayout, Button, Modal, Watermark } from '@zswl/components'
import store from './store'
import Header from './Header'
import IconFont from '@/components/Icon'
import { BackTop, Empty, message, Space } from 'antd'
import { observer, getLatestPublishData, matchRoute, useAppContext, getQuery } from '@zswl/admin'
import { useEffect } from 'react'
import BreadLine from './components/BreadLine'
import moment from 'moment'
import BreadcrumbList, { getPurePath } from './BreadcrumbList'

const { getToken } = App

const currentDate = moment().format('yyyy-MM-DD')
const removeSpaces = () => {
  document.addEventListener('copy', function (event) {
    event.preventDefault() // 取消默认的复制行为
    var copiedText = window.getSelection().toString() // 获取复制的文本
    var trimmedText = copiedText.trim() // 去除空格
    event.clipboardData.setData('text/plain', trimmedText) // 将处理后的文本设置为剪贴板内容
  })
}

function Error() {
  useEffect(() => {
    if (process.env.NODE_ENV !== 'development') {
      getLatestPublishData().then((res) => {
        if (res.time && res.time !== __DATA__.time) {
          Modal.info({
            width: 450,
            title: '检测到系统有更新，请点击升级获取最新版本',
            okText: '升级',
            onOk: () => {
              return new Promise(() => {
                setTimeout(() => {
                  message.success('升级成功,即将刷新页面').then(() => {
                    window.location.reload()
                  })
                }, 3000)
              })
            },
          })
        }
      })
    }
  }, [])
  return (
    <Empty style={{ marginTop: 100 }}>
      <Button type="primary" onClick={() => window.location.reload()}>
        刷新
      </Button>
    </Empty>
  )
}
function Layout({ children, pathname }) {
  const { removeKeepAlive } = useAppContext()
  const params = getQuery()
  const { secret, mithrasClientId, random, timestamp } = params
  const isFormOaPC = random && timestamp && mithrasClientId && secret

  const noHeaderTabsPathList = ['/preview', '/dashboard/overView2']
  const { getUserInfoData } = store

  if (window.location.pathname.indexOf('/dashboard/sso') > -1) {
    return children
  }

  // oa待办
  if (isFormOaPC && !getToken()) {
    window.location.href = `/dashboard/ssoFlow?params=${JSON.stringify(params)}&cbUrl=${pathname}`
    return
  }

  const CurrentDom = () => {
    if (noHeaderTabsPathList.find((i) => window.location.pathname.indexOf(i) > -1)) {
      return children
    }
    const purePath = getPurePath(pathname)
    const notNeedBreadcrumb = [
      '/customerView/detail/:id?',
      '/customerView/detail',
      '/implant/jfBulletinBoard',
    ].includes(purePath)
    return (
      <AppLayout
        resizable={false}
        header={<Header />}
        sideWidth={215}
        mode="overall"
        // tabs={!isProd}
        tabs={{
          beforeClose: (pageList) => {
            const paths = pageList.map((item) => item.pathname)
            removeKeepAlive(paths)
          },
          transform(item) {},
        }}
        breadcrumb={!notNeedBreadcrumb && <BreadcrumbList pathname={pathname} />}
      >
        {children}
        <BackTop target={App.getAppLayoutContainer} visibilityHeight={400} />
      </AppLayout>
    )
  }

  useEffect(() => {
    store.loadOnlyOfficeScript()
    removeSpaces()
  }, [])

  useEffect(() => {
    const { pageUrl, pagePath } = BreadLine.pathnameToPathInfo()
    BreadLine.setBreadList(pagePath, pageUrl)
  }, [pathname])

  return (
    <App
      init={store.init}
      pathname={pathname}
      provider={{
        errorBoundary: {
          fallback: <Error />,
        },
        modal: {
          draggable: true,
        },
      }}
      getMenuItemProps={({ level, icon }) => {
        if (level === 0) {
          return {
            icon: icon ? <IconFont type={icon} style={{ fontSize: 16 }} /> : null,
          }
        }
        return { icon: null }
      }}
    >
      {/* <StartRentTip></StartRentTip> */}
      {CurrentDom()}
      {getUserInfoData?.userName && (
        <Watermark
          content={[
            `${getUserInfoData.userName} (${getUserInfoData.phone?.substr(-4)})`,
            currentDate,
          ]}
          gapX={180}
          gapY={90}
          width={160}
          fontColor={'rgba(0,0,0,0.06)'}
          fontWeight={140}
          rotate={-14}
          fontStyle={'italic'}
        ></Watermark>
      )}
    </App>
  )
}

export default observer(Layout)
