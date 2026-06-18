import { MyWebSocket } from '@/utils/ws'
import { Button, notification, Space, Tag, Divider, message } from 'antd'
import { useEffect, useMemo, useRef, useState } from 'react'
import api from '@/api/layout/messageApi'
import styles from './index.less'
import { App } from '@zswl/components'
import { history } from '@zswl/admin'
import useInterval from '@/utils/hooks/useInterval'

const MsgModal = ({ store }) => {
  const { getUserInfoData } = store
  const ws = useRef()

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

  const readMessage = async (id) => {
    await api.postReadMessage({
      messageChannel: 'PC',
      noticeIds: [id],
    })
  }
  const initWs = (id) => {
    ws.current = new MyWebSocket(`/zswl/message/ws/${id}`)
    ws.current.message('default', (val) => {
      if (val === 'pang') {
        return
      }
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
          },
          duration: null,
          description: (
            <div className={styles.contract_notice}>
              {isContractNotice && (
                <div className={styles.title}>以下合同已投放，请起租，否则将影响财务入账：</div>
              )}
              <div style={{ maxHeight: 360, overflow: 'auto' }}>
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
    if (getUserInfoData?.id) {
      initWs(getUserInfoData?.id)
    }

    return () => {
      ws.current?.removeMessage('default')
      ws.current?.close()
    }
  }, [getUserInfoData?.id])

  return null
}
export default MsgModal
