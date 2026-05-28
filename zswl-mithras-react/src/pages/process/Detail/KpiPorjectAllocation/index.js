import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import KpiPorjectAllocation from '@/pages/kpi/projectAllot/detail/[id$]'

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
