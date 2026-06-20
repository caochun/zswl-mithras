import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import DashboardRadioTabs from '../../../RadioTabs'
import ComponyTotal from './ComponyTotal'
import DeptTotal from './DeptTotal'
import Store from './Store'
import InvestmentDrawer from './InvestmentDrawer'

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
      <CardPanelFieldsFilter title="计划执行情况">
        <DashboardRadioTabs
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
        ></DashboardRadioTabs>
      </CardPanelFieldsFilter>
      <InvestmentDrawer store={store}></InvestmentDrawer>
    </>
  )
}

export default observer(Index)
