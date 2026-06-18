import IconFont from '@/components/Icon'
import { MyWebSocket } from '@/utils/ws'
import { RightOutlined } from '@ant-design/icons'
import {
  Button,
  notification,
  Tabs,
  Tooltip,
  Pagination,
  Empty,
  Space,
  Tag,
  Divider,
  message,
} from 'antd'
import classNames from 'classnames'
import { useEffect, useMemo, useRef, useState } from 'react'
import moment from 'moment'
import api from '@/api/message/messageNotification'
import styles from './index.less'
import { App } from '@zswl/components'
import { history } from '@zswl/admin'
import layoutStore from '@/layout/store'
import useInterval from '@/utils/hooks/useInterval'
import headerStore from '../../../layout/store'

const dateFormat = 'yyyy-MM-DD HH:mm:ss'
const TYPE = {
  APPROVAL_PROCESS: 'icon-icon_approval',
  PAYMENT: 'icon-icon_money',
  PROJ_REVIEW: 'icon-icon_remind',
}

const MsgModal = ({ visible, onChange }) => {
  const [thisWeek, setThisWeek] = useState({})
  const [lastWeek, setLastWeek] = useState({})
  const [tabKey, setTabKey] = useState('1')
  const ref = useRef()

  const queryMessage = async (params, setValue) => {
    const res = await api.postMessageList(params)
    if (res) {
      setValue(res)
    }
  }

  const getWeekData = (page = 1) => {
    queryMessage(
      {
        createStart: moment().startOf('W').format(dateFormat),
        createEnd: moment().endOf('W').format(dateFormat),
        page,
        pageSize: 5,
      },
      setThisWeek
    )
  }

  const getLastWeekData = (page = 1) => {
    queryMessage(
      {
        createStart: moment().startOf('W').subtract(1, 'w').format(dateFormat),
        createEnd: moment().endOf('W').subtract(1, 'w').format(dateFormat),
        page,
        pageSize: 5,
      },
      setLastWeek
    )
  }

  const getData = () => {
    getWeekData()
    getLastWeekData()
  }
  useEffect(() => {
    getData()
  }, [])

  const goContract = (key) => {
    history.push(`/contract/list?contractStatus=TAKE_EFFECT&t=${Date.now()}`)
  }
  const goRentCollection = (collectionId) => {
    history.push(`/afterLease/rentCollection?collectionId=${collectionId}&t=${Date.now()}`)
  }
  const handleAlert = async (key) => {
    await api.contractRentNoticeDelay()
    message.success('操作成功')
    notification.close(key)
  }

  const readMessage = async (id, callback) => {
    await api.postReadMessage({
      messageChannel: 'PC',
      noticeIds: [id],
    })
    headerStore.getNoReadNum()
    callback && callback()
  }
  const ws = useRef()
  const initWs = (id) => {
    ws.current = new MyWebSocket(`/zswl/message/ws/${id}`)
    ws.current.message('default', (val) => {
      if (val === 'pang') {
        return
      }
      headerStore.getNoReadNum()
      getData()
      const { bizInfo, type, title, content } = val
      const { other } = bizInfo ? JSON.parse(bizInfo) : {}
      if ([1, 3].includes(type)) {
        const notificationKey = `message_${Date.now()}`
        notification.open({
          key: notificationKey,
          message: App.matchOption('messageTypeEnum', other?.messageType).label,
          description: title?.replace(/\\n/g, '\n'),
          btn: (
            <Button
              type="primary"
              size="small"
              onClick={() => {
                readMessage(id)
                window.location.href = unescape(other?.businessUrl)
                notification.close(notificationKey)
              }}
            >
              查看详情
            </Button>
          ),
        })
      } else if (type === 5) {
        const contractList = content?.split('$')
        const notificationKey = `contract_notice`
        const isContractNotice = other?.popUpType === 'CONTRACT_RENT'
        notification.close(notificationKey)
        notification.open({
          className: isContractNotice && styles.contract_notice_notification,
          btn: (
            <div className={styles.btnGroup}>
              <Space>
                {isContractNotice && (
                  <Button onClick={() => handleAlert(notificationKey)}>十分钟后提醒我</Button>
                )}
                {isContractNotice && (
                  <Button type="primary" onClick={() => goContract(notificationKey)}>
                    去起租
                  </Button>
                )}
                {!isContractNotice && other?.collectionId && (
                  <Button type="primary" onClick={() => goRentCollection(other?.collectionId)}>
                    查看项目
                  </Button>
                )}
              </Space>
            </div>
          ),
          key: notificationKey,
          message: isContractNotice ? '起租提示' : '项目质押提醒',
          style: {
            width: 480,
            height: 400,
          },
          duration: null,
          description: (
            <div className={styles.contract_notice}>
              {isContractNotice && (
                <div className={styles.title}>以下合同已投放，请起租，否则将影响财务入账：</div>
              )}
              <div>
                {contractList?.map((item, index) => {
                  return (
                    <div key={index} className={styles.item}>
                      <div style={{ marginBottom: 5 }}>{item}</div>
                    </div>
                  )
                })}
              </div>
              <Divider style={{ marginBottom: 0 }}></Divider>
            </div>
          ),
        })
      }
    })
  }

  useInterval(() => {
    if (ws.current && ws.current.status === 2) {
      ws.current.ws.send('ping')
    }
  }, 1000 * 10)
  useEffect(() => {
    if (layoutStore?.getUserInfoData?.id) {
      initWs(layoutStore?.getUserInfoData?.id)
    }

    return () => {
      ws.current?.removeMessage('default')
      ws.current?.close()
    }
  }, [layoutStore?.getUserInfoData?.id])

  const handleTitle = (val) => {
    return val?.replace(/\\n/g, '\n')
  }

  const renderItem = (arr) => {
    return (
      <>
        {arr?.list?.length > 0 ? (
          arr.list.map((item, index) => {
            const { id, title, gmtCreate, readFlag, content, bizInfo, overtimeFlag, type } = item
            const { other } = bizInfo ? JSON.parse(bizInfo) : {}
            return (
              <div key={index} className={styles.item}>
                <div className={styles.icon}>
                  <IconFont type={TYPE[other?.noticeSource || 'PROJ_REVIEW']} />
                </div>
                <div className={styles.msg}>
                  <div className={styles.time}>
                    {gmtCreate ? moment(gmtCreate).format('yyyy-MM-DD HH:mm:ss') : '-'}
                  </div>
                  <div className={classNames(styles.type, { [styles.isRead]: !readFlag })}>
                    {other?.messageType || '-'}
                  </div>
                  <Tooltip title={handleTitle(title)}>
                    <div className={styles.title}>{handleTitle(title)}</div>
                  </Tooltip>
                  <Space>
                    <a
                      className={styles.a}
                      onClick={async () => {
                        await readMessage(id)
                        window.location.href = unescape(other?.businessUrl)
                      }}
                    >
                      {content || '-'}
                    </a>
                    {overtimeFlag === 1 && type === 3 && <Tag color="red">已超时</Tag>}
                  </Space>
                </div>
              </div>
            )
          })
        ) : (
          <Empty style={{ marginTop: 100 }}></Empty>
        )}
      </>
    )
  }
  const thisWeekDom = useMemo(() => {
    return renderItem(thisWeek)
  }, [thisWeek])
  const lastWeekDom = useMemo(() => {
    return renderItem(lastWeek)
  }, [lastWeek])

  useEffect(() => {
    const onClick = (e) => {
      if (ref.current && visible && !ref.current.contains(e.target)) {
        onChange(false)
      }
    }

    if (visible) {
      setTimeout(() => document.addEventListener('click', onClick), 0)
    }

    return () => document.removeEventListener('click', onClick)
  }, [visible])

  const PaginationComp = ({ type }) => {
    if (type === '1') {
      return thisWeek.list?.length > 0 ? (
        <div className={styles.pagination}>
          共{thisWeek?.total}条
          <Pagination
            onChange={(v) => getWeekData(v)}
            simple
            total={thisWeek?.total}
            pageSize={5}
            className={styles.pagination}
            current={thisWeek.currentPage}
          />
        </div>
      ) : null
    } else {
      return lastWeek.list?.length > 0 ? (
        <div className={styles.pagination}>
          共{lastWeek?.total}条
          <Pagination
            simple
            onChange={(v) => getLastWeekData(v)}
            total={lastWeek?.total}
            pageSize={5}
            current={lastWeek.currentPage}
          />
        </div>
      ) : null
    }
  }

  return (
    <div
      id="msgModal"
      ref={ref}
      className={styles.modalWrap}
      style={{ display: visible ? 'block' : 'none' }}
    >
      <div className={styles.tabsWrap}>
        <Tabs defaultActiveKey={tabKey} onChange={setTabKey}>
          <Tabs.TabPane key="1" tab="本周">
            <div className={styles.content}>{thisWeekDom}</div>
          </Tabs.TabPane>
          <Tabs.TabPane key="2" tab="上周">
            <div className={styles.content}>{lastWeekDom}</div>
          </Tabs.TabPane>
        </Tabs>

        <div
          className={styles.seeMore}
          onClick={() => {
            onChange(false)
            history.push('/msgNotification')
          }}
        >
          查看全部 <RightOutlined />
        </div>
      </div>
      <div className={styles.footer}>
        <PaginationComp type={tabKey}></PaginationComp>
      </div>
    </div>
  )
}
export default MsgModal
