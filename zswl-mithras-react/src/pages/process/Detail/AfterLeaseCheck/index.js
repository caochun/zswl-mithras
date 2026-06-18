import { useEffect, useMemo, useState } from 'react'
import { observer } from '@zswl/admin'
import CreatePlan from '@/components/AfterLease/CheckPlanCreate'
import PlanDetail from '@/components/AfterLease/CheckPlanDetail'
import PlanReport from '@/components/AfterLease/CheckPlanTemplate'

const AfterLeaseCheck = ({ modelKey, canEditFlag, id, businessVersion }) => {
  const renderContractType = useMemo(() => {
    if (
      modelKey == 'NewAfterLeaseCheckPlanPublishCreateFlow' ||
      modelKey == 'NewAfterLeaseCheckPlanPublishModifyFlow'
    ) {
      return (
        <CreatePlan
          params={{ id }}
          query={{
            businessVersion,
            canEditFlags: canEditFlag ? 'true' : 'false',
          }}
        />
      )
    }
    // if (modelKey == 'NewAfterLeaseCheckReportFlow') {
    //   return (
    //     <PlanReport
    //       params={{ id }}
    //       query={{
    //         businessVersion,
    //         canEditFlags: canEditFlag ? 'true' : 'false',
    //       }}
    //     />
    //   )
    // }
    if (modelKey == 'NewAfterLeaseCheckPlanFinishFlow') {
      return (
        <PlanDetail
          params={{ id }}
          query={{
            businessVersion,
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
