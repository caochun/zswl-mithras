import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { KpiProjectAllotDetail as KpiPorjectAllocation } from '@/components/Kpi/ProjectAllotEntries'

const Index = (props) => {
  const { id, businessVersion, canEditFlag, modelKey, curTaskActivityIds, taskStatus } = props

  const source = {
    KpiProjectDistributionCreateFlow: 'unDeal',
    KpiProjectDistributionModifyFlow: 'adjust',
    KpiProjectDistributionTransferFlow: 'adjust',
  }[modelKey]

  const Content = useMemo(() => {
    return (
      <div>
        <KpiPorjectAllocation
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
  }, [canEditFlag, businessVersion, canEditFlag, curTaskActivityIds, taskStatus])

  return Content
}
export default observer(Index)
