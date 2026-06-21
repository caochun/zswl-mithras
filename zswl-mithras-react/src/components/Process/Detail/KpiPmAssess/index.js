import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { KpiPmAssessDetailContent as PmAssess } from '@/components/Kpi/PmAssessDetailContentEntries'

const ProcessDetailKpiPmAssess = (props) => {
  const { canEditFlag } = props
  const renderContractType = useMemo(() => {
    return (
      <div>
        <PmAssess {...props} canEditFlags={canEditFlag ? 'true' : 'false'} />
      </div>
    )
  }, [canEditFlag])

  return renderContractType
}
export default observer(ProcessDetailKpiPmAssess)
