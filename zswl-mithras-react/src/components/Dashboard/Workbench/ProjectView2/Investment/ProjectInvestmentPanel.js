import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import DashboardRadioTabs from '../../../RadioTabs'
import ProjectInvestmentCompanyTotal from './CompanyTotal/ProjectInvestmentCompanyTotal'
import ProjectInvestmentDrawer from './InvestmentDrawer/ProjectInvestmentDrawer'
import ProjectInvestmentDepartmentTotal from './DeptTotal/ProjectInvestmentDepartmentTotal'
import Store from './Store'

const ProjectInvestmentPanel = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { activityKey, setActivityKey } = store

  const tabItem = useMemo(() => {
    return (
      <div>
        <ProjectInvestmentCompanyTotal store={store}></ProjectInvestmentCompanyTotal>
        <div style={{ height: 20 }}></div>
        <ProjectInvestmentDepartmentTotal store={store}></ProjectInvestmentDepartmentTotal>
      </div>
    )
  }, [store])

  return (
    <>
      <CardPanelFieldsFilter title="投放情况">
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
      <ProjectInvestmentDrawer store={store}></ProjectInvestmentDrawer>
    </>
  )
}

export default observer(ProjectInvestmentPanel)
