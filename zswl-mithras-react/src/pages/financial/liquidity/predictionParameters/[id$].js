import { useState } from 'react'
import { Page, Tabs } from '@zswl/components'
import TeamAccount from './TeamAccount'
import Parameters from './Parameters'
import { observer } from '@zswl/admin'

/**
 * 预测参数配置页面
 * @param {Object} props 组件属性
 * @returns {ReactElement} 预测参数配置组件
 */
const PredictionParameters = observer((props) => {
  const [activeTab, setActiveTab] = useState('1')

  // Tab 配置
  const tabItems = [
    {
      key: '1',
      label: '回款账户',
      children: <TeamAccount />,
    },
    {
      key: '2',
      label: '其他参数',
      children: <Parameters />,
    },
  ]

  return (
    <Page>
      <div className="prediction-parameters">
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems} />
      </div>
    </Page>
  )
})

export default PredictionParameters

// 样式文件
