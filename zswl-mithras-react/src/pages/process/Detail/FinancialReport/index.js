import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import FinancialReport from '@/components/Report/FinancialReportApproval'

const Index = (props) => {
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
export default observer(Index)
