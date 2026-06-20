import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { Tabs } from 'antd'
import { useState } from 'react'
import RiskStrategy from './IndicatorManage'
import RelateMonitor from './RelateMonitor/RiskStrategyRelateMonitor'
import ConcentrationControl from './ConcentrationControl/RiskStrategyConcentrationControl'

const { TabPane } = Tabs
function Index() {
  const [active, setActive] = useState('1')
  const onChange = (key) => {
    setActive(key)
  }

  return (
    <Page>
      <Tabs defaultActiveKey="1" activeKey={active} onChange={onChange}>
        <TabPane tab="指标管理" key="1">
          <RiskStrategy />
        </TabPane>
        <TabPane tab="集中度管理" key="2">
          <ConcentrationControl />
        </TabPane>
        <TabPane tab="关联交易监测" key="3">
          <RelateMonitor />
        </TabPane>
      </Tabs>
    </Page>
  )
}

export default observer(Index)
