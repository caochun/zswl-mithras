import { observer } from '@zswl/admin'
import { DashboardTitle as Title } from '@/components/Dashboard/DashboardEntries'
import PlanExecute from './PlanExecute'
import Investment from './Investment'
import RentRecovery from './RentRecovery'
import { isFundDept } from '@/utils'
import { DashboardProjectInfo as ProjectInfo } from '@/components/Dashboard/DashboardEntries'
import { getUserInfo } from '@/utils'

const Index = ({ title, iconType }) => {
  const is_wujie = getUserInfo().id === 49

  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <Investment />
      <div style={{ height: 20 }}></div>
      <PlanExecute />
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

export default observer(Index)
