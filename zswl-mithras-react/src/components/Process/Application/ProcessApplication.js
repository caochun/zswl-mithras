import { observer } from '@zswl/admin'
import store from './store'
import { Page } from '@zswl/components'
import { Spin, Tabs } from 'antd'
import styles from './index.less'
import Apply from './Apply'
import Revocation from './Revocation'
import Sendback from './Sendback'
import Prepare from '../PrepareList'
import Finish from './Finish'
import { useEffect } from 'react'

const { TabPane } = Tabs
function ProcessApplication() {
  const { processData, loading } = store
  const onChange = (key) => {
    store.myProcessCount()
  }
  useEffect(() => {
    // 这个接口太慢了，太影响本地开发
    store.myProcessCount()
  }, [])
  return (
    <Page>
      <Spin spinning={['prod', 'preSvc', 'uat'].includes(__ENV__) ? loading : false}>
        <div className={styles.application}>
          <Tabs defaultActiveKey="1" onChange={onChange} destroyInactiveTabPane>
            <TabPane tab={`待发起`} key="0">
              <Prepare />
            </TabPane>
            <TabPane tab={`申请中(${processData.applyingCount || '-'})`} key="1">
              <Apply />
            </TabPane>
            <TabPane tab={`我的撤回(${processData.withdrawCount || '-'})`} key="2">
              <Revocation />
            </TabPane>
            <TabPane tab={`审批退回(${processData.backToStepCount || '-'})`} key="3">
              <Sendback />
            </TabPane>
            <TabPane tab={`审批结束(${processData.finishCount || '-'})`} key="4">
              <Finish />
            </TabPane>
          </Tabs>
        </div>
      </Spin>
    </Page>
  )
}

export default observer(ProcessApplication)
