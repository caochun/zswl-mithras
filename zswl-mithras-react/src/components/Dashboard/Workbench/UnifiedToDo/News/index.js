import { useEffect, useState } from 'react'
import { observer, history } from '@zswl/admin'
import { Space, List, Tooltip, Modal, message, Divider } from 'antd'
import Api from '@/api/dashboard/workbenchMessageApi'
import DashboardRadioTabs from '../../../RadioTabs'
import { CheckOutlined, RightOutlined } from '@ant-design/icons'
import { sleep } from '@/utils'
import styles from './index.less'

const MsgList = ({ data, init }) => {
  const getBizInfoOtherKey = (item, key) => {
    const jsonData = item.bizInfo ? JSON.parse(item.bizInfo).other : {}
    return key ? jsonData[key] : jsonData
  }
  const readMessage = async (id) => {
    await Api.postReadMessage({
      messageChannel: 'PC',
      noticeIds: [id],
    })
  }

  const openLink = async (item) => {
    const { id } = item
    const { businessUrl, messageType } = getBizInfoOtherKey(item)
    await readMessage(id)
    if (!businessUrl) return
    if (messageType?.includes('待发起')) {
      window.open(`${unescape(businessUrl)}`)
    } else {
      window.open(`${unescape(businessUrl)}&tab=sendback`)
    }
    init?.()
  }

  return (
    <div className={styles.listWrap}>
      <List
        size="small"
        dataSource={data}
        renderItem={(item) => (
          <div className={styles.row} onClick={() => openLink(item)}>
            <div className={styles.type}>
              {item.bizInfo ? (
                <Tooltip title={getBizInfoOtherKey(item, 'messageType')} placement="topLeft">
                  {getBizInfoOtherKey(item, 'messageType')}
                </Tooltip>
              ) : (
                '-'
              )}
            </div>
            <div className={styles.title}>{item.title}</div>
            <div className={styles.time}>{item.gmtCreate}</div>
          </div>
        )}
      ></List>
    </div>
  )
}

const Index = ({ store }) => {
  const [activeKey, setActiveKey] = useState(2)
  const [msgCount, setMsgCount] = useState([])
  const [msgData, setMsgData] = useState([])

  const readAllMessage = () => {
    Modal.confirm({
      title: `是否将所有消息设为已读？`,
      onOk: async () => {
        await Api.postReadAllMessage({ messageChannel: 'PC' })
        // 异步延迟
        await sleep(1000)
        init()
        message.success('操作成功')
      },
    })
  }

  const seeMore = () => {
    history.push(`/msgNotification`)
  }

  const getMsgCount = async () => {
    const res = await Promise.all([
      Api.postMessageList({ pageSize: 1, page: 1, needRead: '' }),
      Api.postMessageList({ pageSize: 1, page: 1, needRead: 2 }),
    ])
    setMsgCount(res?.map((item) => item.total))
  }

  // needRead :''全部，2 未读，1已读
  const getData = async () => {
    const res = await Api.postMessageList({ pageSize: 5, page: 1, needRead: activeKey })
    setMsgData(res?.list || [])
  }

  const init = () => {
    getData()
    getMsgCount()
    store?.updateMsgCount()
  }

  useEffect(() => {
    init()
  }, [])

  useEffect(() => {
    init()
  }, [activeKey])

  return (
    <div className={styles.content}>
      <DashboardRadioTabs
        destroyInactiveTabPane
        tabBarExtraContent={
          <Space size={20}>
            <div onClick={readAllMessage} className={styles.action_row}>
              <CheckOutlined></CheckOutlined>
              全部已读
            </div>
            <div onClick={seeMore} className={styles.action_row}>
              查看更多<RightOutlined></RightOutlined>
            </div>
          </Space>
        }
        activeKey={activeKey}
        onChange={setActiveKey}
        items={[
          {
            label: `全部(${msgCount[0] ?? 0})`,
            key: '',
            children: <MsgList data={msgData} init={init} />,
          },
          {
            label: `未读(${msgCount[1] ?? 0})`,
            key: 2,
            children: <MsgList data={msgData} init={init} />,
          },
        ]}
      ></DashboardRadioTabs>
    </div>
  )
}

export default observer(Index)
