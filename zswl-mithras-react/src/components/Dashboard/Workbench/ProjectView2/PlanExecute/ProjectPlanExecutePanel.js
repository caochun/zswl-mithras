import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import DashboardRadioTabs from '../../../RadioTabs'
import ProjectPlanExecuteCompanyTotal from './CompanyTotal/ProjectPlanExecuteCompanyTotal'
import ProjectPlanExecuteDepartmentTotal from './DeptTotal/ProjectPlanExecuteDepartmentTotal'
import Store from './Store'
import ProjectPlanExecuteDrawer from './InvestmentDrawer/ProjectPlanExecuteDrawer'

const ProjectPlanExecutePanel = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { activityKey, setActivityKey } = store

  const tabItem = useMemo(() => {
    return (
      <div>
        <ProjectPlanExecuteCompanyTotal store={store}></ProjectPlanExecuteCompanyTotal>
        <div style={{ height: 20 }}></div>
        <ProjectPlanExecuteDepartmentTotal store={store}></ProjectPlanExecuteDepartmentTotal>
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
      <ProjectPlanExecuteDrawer store={store}></ProjectPlanExecuteDrawer>
    </>
  )
}

export default observer(ProjectPlanExecutePanel)
