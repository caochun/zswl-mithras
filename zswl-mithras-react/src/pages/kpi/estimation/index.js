import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { cloneElement } from 'react'
import Contract from './contract'
import ProjectManagerPrize from './projectManagerPrize'
import ProjectManagerProfit from './projectManagerProfit'
import DepartmentalPool from './departmentalPool'
import { Tabs } from 'antd'

const Index = ({ pathname }) => {
  const items = [
    {
      label: '合同维度',
      children: <Contract></Contract>,
    },
    {
      label: '项目经理奖金',
      children: <ProjectManagerPrize></ProjectManagerPrize>,
    },
    {
      label: '项目经理利润完成率',
      children: <ProjectManagerProfit></ProjectManagerProfit>,
    },
    {
      label: '部门池',
      children: <DepartmentalPool></DepartmentalPool>,
    },
  ]
  const newItems = items.map(({ label, children }) => {
    return {
      label,
      key: label,
      children: cloneElement(children, {
        pathname,
      }),
    }
  })
  return (
    <Page>
      <Tabs destroyInactiveTabPane items={newItems} />
    </Page>
  )
}
export default observer(Index)
