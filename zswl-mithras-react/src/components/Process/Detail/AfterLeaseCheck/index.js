import { useEffect, useMemo, useState } from 'react'
import { observer } from '@zswl/admin'
import { AfterLeaseCheckPlanCreate as CreatePlan } from '@/components/AfterLease/CheckPlanCreateEntries'
import { AfterLeaseCheckPlanDetail as PlanDetail } from '@/components/AfterLease/CheckPlanDetailEntries'
import { AfterLeaseCheckPlanTemplate as PlanReport } from '@/components/AfterLease/CheckPlanTemplateEntries'

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
