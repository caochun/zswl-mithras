import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { KpiProjectAllotDetail as KpiProjectAllocation } from '@/components/Kpi/ProjectAllotDetailEntries'

const ProcessDetailKpiProjectAllocation = (props) => {
  const { id, businessVersion, canEditFlag, modelKey, curTaskActivityIds, taskStatus } = props

  const source = {
    KpiProjectDistributionCreateFlow: 'unDeal',
    KpiProjectDistributionModifyFlow: 'adjust',
    KpiProjectDistributionTransferFlow: 'adjust',
  }[modelKey]

  const Content = useMemo(() => {
    return (
      <div>
        <KpiProjectAllocation
          params={{
            id,
          }}
          query={{
            modelKey,
            source,
            businessVersion,
            canEditFlags: canEditFlag,
            curTaskActivityIds,
            taskStatus,
          }}
        />
      </div>
    )
  }, [id, modelKey, source, businessVersion, canEditFlag, curTaskActivityIds, taskStatus])

  return Content
}
export default observer(ProcessDetailKpiProjectAllocation)
