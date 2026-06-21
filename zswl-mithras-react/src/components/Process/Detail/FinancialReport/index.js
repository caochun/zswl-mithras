import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { FinancialReportApproval as FinancialReport } from '@/components/Report/FinancialReportApprovalEntries'

const ProcessDetailFinancialReport = (props) => {
  const { canEditFlag, subModule, id, businessVersion, tab } = props
  const renderContractType = useMemo(() => {
    return (
      <FinancialReport
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          subModule,
          businessVersion,
          tab,
        }}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(ProcessDetailFinancialReport)
