import { observer } from '@zswl/admin'
import Title from '../../Title'
import ProjectPlanExecutePanel from './PlanExecute/ProjectPlanExecutePanel'
import ProjectInvestmentPanel from './Investment/ProjectInvestmentPanel'
import RentRecovery from './RentRecovery'
import { getUserInfo, isFundDept } from '@/utils'
import ProjectInfo from '../../ProjectView/ProjectInfo/DashboardProjectInfo'

const WorkbenchProjectOperationsView = ({ title, iconType }) => {
  const is_wujie = getUserInfo().id === 49

  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <ProjectInvestmentPanel />
      <div style={{ height: 20 }}></div>
      <ProjectPlanExecutePanel />
      <div style={{ height: 20 }}></div>
      <RentRecovery />
      {isFundDept() && !is_wujie && (
        <>
          <div style={{ height: 20 }}></div>
          <ProjectInfo />
        </>
      )}
    </div>
  )
}

export default observer(WorkbenchProjectOperationsView)
