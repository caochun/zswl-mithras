import { getQuery, observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import store from './store'
import { Tabs, Spin } from 'antd'
import styles from './index.less'
import Approval from './Approval'
import Pending from './Pending'
import SendDuplicate from './SendDuplicate'
import { useEffect, useState } from 'react'
const { TabPane } = Tabs
function Index() {
  const { receiveData, loading } = store
  const [curTab, setCurTab] = useState('pending')
  const { enterpriseName, tag } = getQuery()

  const onChange = (key) => {
    store.myReceiveCount()
    setCurTab(key)
  }
  useEffect(() => {
    store.myReceiveCount()
  }, [])
  return (
    <Page>
      <Spin spinning={loading}>
        <div className={styles.receive}>
          <Tabs onChange={onChange} activeKey={curTab} destroyInactiveTabPane>
            <TabPane tab={`待审批(${receiveData.todoCount || '-'})`} key="pending">
              <Pending enterpriseName={enterpriseName} curTab={curTab} />
            </TabPane>
            <TabPane tab={`已审批(${receiveData.doneCount || '-'})`} key="approval">
              <Approval enterpriseName={enterpriseName} curTab={curTab} />
            </TabPane>
            <TabPane tab={`抄送我的(${receiveData.ccCount || '-'})`} key="sendDuplicate">
              <SendDuplicate enterpriseName={enterpriseName} curTab={curTab} />
            </TabPane>
          </Tabs>
        </div>
      </Spin>
    </Page>
  )
}

export default observer(Index)
