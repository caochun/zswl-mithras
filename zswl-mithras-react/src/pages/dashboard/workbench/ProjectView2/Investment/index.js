import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { RadioTabs } from '@/components'
import { CardPanelFieldsFilter } from '@/pages/dashboard/workbench/components'
import ComponyTotal from './ComponyTotal'
import InvestmentDrawer from './InvestmentDrawer'
import DeptTotal from './DeptTotal'
import Store from './Store'

const Index = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { activityKey, setActivityKey } = store

  const tabItem = useMemo(() => {
    return (
      <div>
        <ComponyTotal store={store}></ComponyTotal>
        <div style={{ height: 20 }}></div>
        <DeptTotal store={store}></DeptTotal>
      </div>
    )
  }, [store])

  return (
    <>
      <CardPanelFieldsFilter title="投放情况">
        <RadioTabs
          active={activityKey}
          onChange={setActivityKey}
          items={[
            {
              label: '本月',
              key: 'MONTH',
              children: tabItem,
            },
            {
              label: '本年',
              key: 'YEAR',
              children: tabItem,
            },
          ]}
        ></RadioTabs>
      </CardPanelFieldsFilter>
      <InvestmentDrawer store={store}></InvestmentDrawer>
    </>
  )
}

export default observer(Index)
