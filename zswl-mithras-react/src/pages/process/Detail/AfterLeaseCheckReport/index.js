import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import PlanReport from '@/components/AfterLease/CheckPlanTemplate'

const AfterLeaseCheck = ({ modelKey, canEditFlag, id, businessVersion, taskActivityId }) => {
  const renderContractType = useMemo(() => {
    if (
      modelKey == 'NewAfterLeaseCheckReportFlow' ||
      modelKey === 'NewAfterLeaseCheckReportCommonlyFlow'
    ) {
      return (
        <PlanReport
          params={{ id }}
          query={{
            businessVersion,
            taskActivityId,
            canEditFlags: canEditFlag ? 'true' : 'false',
          }}
        />
      )
    }

    return null
  }, [modelKey, canEditFlag, id, businessVersion])

  return renderContractType
}
export default observer(AfterLeaseCheck)
