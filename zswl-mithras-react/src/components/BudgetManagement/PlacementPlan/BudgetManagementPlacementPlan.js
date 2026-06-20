import React, { useState } from 'react'
import { Tabs } from 'antd'
import PlacementPlanTab from './PlacementPlanTab'
import ProjectReportTab from './ProjectReportTab'
import { Page } from '@zswl/components'

const PlacementPlan = () => {
  const [activeKey, setActiveKey] = useState('plan')

  return (
    <Page>
      <Tabs
        activeKey={activeKey}
        onChange={setActiveKey}
        items={[
          {
            key: 'plan',
            label: '投放计划',
            children: <PlacementPlanTab />,
          },
          {
            key: 'report',
            label: '项目周报',
            children: <ProjectReportTab />,
          },
        ]}
      />
    </Page>
  )
}

export default PlacementPlan
