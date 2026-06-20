import { BudgetManagementPlacementPlanDetail as MonthPlanEvent } from '@/components/BudgetManagement/PlacementPlanEntries'
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
    mainModule,
  } = props
  const [planId, deptId] = id.split('-')
  return (
    <>
      <MonthPlanEvent
        processInstanceId={processInstanceId}
        {...props}
        params={{ id: planId, deptId, taskActivityId, processName, mainModule }}
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
