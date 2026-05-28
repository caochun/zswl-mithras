import store from './store'
import { App, AppLayout, Button, Modal, Watermark } from '@zswl/components'
import { Dropdown, Badge, Space } from 'antd'
import { UserOutlined } from '@ant-design/icons'
import ChangePasswordModal from './ChangePasswordModal'
import { observer } from '@zswl/admin'
import { useEffect, useState, useRef, useMemo } from 'react'
import IconFont from '@/components/Icon'
import styles from './style.less'
import OCR from './OCR'
import FastLogin from './FastLogin'
import ToolsModal from './ToolsModal'
import ProjProfitTool from './ProjProfitTool'
import Banner from './Banner'
import { getUserJobs, jumpZhongDeng } from '@/utils'
import BannerStore from './Banner/store'
import Msg from './Msg'
import TaskFloat from './TaskFloat'

const { Trigger } = AppLayout

function Header() {
  const { getUserInfoData } = store
  const [open, setOpen] = useState(false)
  const dropDownRef = useRef()

  const bannerStore = useMemo(() => {
    return new BannerStore()
  }, [])

  //"会计利润测算" 权限
  const hasProfitcalculate = getUserJobs()?.some((item) => item.jobCode === 'profitcalculate')

  const menu = [
    {
      key: '1',
      label: (
        <div onClick={() => setOpen(true)} style={{ padding: '0 8px 0 8px' }}>
          用户信息
        </div>
      ),
    },
    {
      key: '2',
      label: (
        <div onClick={store.logout} style={{ padding: '0 8px 0 8px' }}>
          退出
        </div>
      ),
    },
  ]

  return (
    <div className={styles.headerWrap}>
      <div className={styles.header}>
        <div className={styles.logoWrap}>
          <IconFont type="icon-rongzuyichanpinLOGO" className={styles.logo}></IconFont>
          <div style={{ marginLeft: 20 }}>
            <Trigger></Trigger>
          </div>
          {/* <div className={styles.topBar}>
            <TopNav limit={12} />
          </div> */}
        </div>
        <div className={styles.user}>
          <FastLogin />
          <Space>
            <div onClick={jumpZhongDeng} className={styles.row}>
              <div className={styles.ocr}>
                <img
                  style={{ width: 20, height: 20 }}
                  src={`/public/avatar/zdw.png`}
                  alt="avatar"
                />
              </div>
              {/* <IconFont
                type="icon-zhongdengwangdengji"
                className={styles.ocr}
                style={{ color: 'transparent', background: 'transparent' }}
              /> */}
            </div>
            <TaskFloat />

            <div onClick={store.createOcr.open}>
              <IconFont type="icon-icon_OCR1" className={styles.ocr} />
            </div>
            <div onClick={store.toolModal.open}>
              <IconFont type="icon-icon_IRR" className={styles.ocr} />
            </div>
            {hasProfitcalculate && (
              <div onClick={store.projProfitTool.open}>
                <IconFont type="icon-icon_cesuan" className={styles.ocr} />
              </div>
            )}
            <div onClick={bannerStore.$bannerModal.open} className={styles.row}>
              <IconFont type="icon-announcement" className={styles.ocr} />
              <div className={styles.row_title}>公告</div>
            </div>
            <span>{getUserInfoData?.userName}</span>
            <div
              ref={(node) => {
                dropDownRef.current = node
              }}
            >
              <Dropdown
                menu={{
                  items: menu,
                }}
                placement="bottomRight"
                arrow
                getPopupContainer={() => {
                  return dropDownRef.current
                }}
              >
                <UserOutlined className={styles.sysUser} />
              </Dropdown>
            </div>
          </Space>
        </div>
      </div>
      <ChangePasswordModal open={open} callBack={() => setOpen(false)} />
      <OCR store={store}></OCR>
      <ToolsModal store={store} />
      <ProjProfitTool store={store} />
      <Msg store={store}></Msg>
      <Banner store={bannerStore} />
    </div>
  )
}

export default observer(Header)
