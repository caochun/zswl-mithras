import { observer } from '@zswl/admin'
import Title from '../../Title'
import PlanExecute from './PlanExecute'
import Investment from './Investment'
import RentRecovery from './RentRecovery'
import { isFundDept } from '@/utils'
import ProjectInfo from '../../ProjectView/ProjectInfo'
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
