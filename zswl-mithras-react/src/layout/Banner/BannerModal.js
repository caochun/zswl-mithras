import { observer } from '@zswl/admin'
import { Form, Modal, Button } from '@zswl/components'
import styles from './index.less'
import { useEffect, useMemo } from 'react'
import { List, Space, Pagination } from 'antd'
import ViewContent from './ViewContent'
import EditContent from './EditContent'
import { isInformationpost } from '@/utils'

function Index({ store }) {
  const [form] = Form.useForm()
  const {
    pageStatus,
    curBannerId,
    allBannerList,
    bannerDetail,
    initLoading,
    bannerListData,
    PAGESIZE,
  } = store

  const Content = useMemo(() => {
    if (!curBannerId) {
      return null
    }
    if (pageStatus === 'view') {
      return <ViewContent store={store}></ViewContent>
    } else {
      return <EditContent form={form} store={store} mainId={curBannerId}></EditContent>
    }
  }, [pageStatus, curBannerId, form])

  useEffect(() => {
    store.getBannerDetail(curBannerId)
  }, [curBannerId, store])

  const Footer = useMemo(() => {
    if (!isInformationpost()) {
      return { footer: null }
    }
    if (!curBannerId) {
      return {
        footer: (
          <Button type="primary" onClick={store.handleCreate} key="create">
            新建
          </Button>
        ),
      }
    }
    if (pageStatus === 'view') {
      return {
        footer: (
          <Space>
            <Button type="primary" onClick={store.handleCreate} key="create">
              新建
            </Button>
            <Button onClick={store.handleSetTop} key="top">
              {bannerDetail.top == 1 ? '取消置顶' : '置顶'}
            </Button>
            <Button onClick={store.handleEdit} key="edit">
              编辑
            </Button>
            <Button danger onClick={store.handleDelete} key="delete">
              删除
            </Button>
          </Space>
        ),
      }
    }
    if (pageStatus === 'create') {
      return {
        footer: (
          <Space>
            <Button key={'cancel'} onClick={store.handleCancel}>
              取消
            </Button>
            <Button key={'submit'} type="primary" onClick={store.$bannerModal.submit}>
              确定
            </Button>
          </Space>
        ),
      }
    }
  }, [pageStatus, curBannerId, bannerDetail, store])

  return (
    <Modal
      title={'系统公告'}
      store={store.$bannerModal}
      bodyStyle={{ paddingLeft: 0, paddingRight: 0, minHeight: 300 }}
      destroyOnClose
      width={800}
      {...Footer}
    >
      <div className={styles.bannerModal}>
        <div className={styles.left}>
          <List
            className={styles.list}
            loading={initLoading}
            itemLayout="horizontal"
            dataSource={allBannerList}
            renderItem={(item, index) => (
              <List.Item className={curBannerId === item.id ? styles.itemActive : ''}>
                <List.Item.Meta
                  title={<div className={styles.title}>{item.title}</div>}
                  description={
                    <div className={styles.descWrap}>
                      <div className={styles.description}>{item.createTime}</div>
                      {item.top == 1 ? <div className={styles.top}>🔝</div> : null}
                    </div>
                  }
                  onClick={() => store.onclickItem(item)}
                />
              </List.Item>
            )}
          />
          <div className={styles.pagination}>
            <Pagination
              simple
              hideOnSinglePage
              onChange={(v) => store.getAllBannerList(v)}
              pageSize={PAGESIZE}
              total={bannerListData?.total}
              current={bannerListData.currentPage}
            />
          </div>
        </div>
        <div className={styles.right}>{Content}</div>
      </div>
    </Modal>
  )
}

export default observer(Index)
