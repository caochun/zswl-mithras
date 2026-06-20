import { BudgetManagementPlacementPlanWeekDetail as WeekPlanEvent } from '@/components/BudgetManagement/PlacementPlanEntries'
import { observer } from '@zswl/admin'

const ProjectReview = (props) => {
  const {
    canEditFlag,
    processInstanceId,
    businessVersion,
    id,
    isNewLayout,
    processName,
    taskActivityId,
    modelKey,
  } = props
  const [planId, deptId] = id.split('-')
  return (
    <>
      <WeekPlanEvent
        processInstanceId={processInstanceId}
        {...props}
        params={{ id: planId, deptId, taskActivityId, processName }}
        modelKey={modelKey}
        query={{
          canEditFlag: canEditFlag ? 'true' : 'false',
          businessVersion,
        }}
      />
    </>
  )
}
export default observer(ProjectReview)
