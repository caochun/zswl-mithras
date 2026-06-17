import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import FinancialPaymentBatch from '@/components/Financial/PaymentBatchApproval'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, modelKey, curTaskActivityIds } = props
  const renderContractType = useMemo(() => {
    return (
      <FinancialPaymentBatch
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          businessVersion,
          modelKey,
        }}
      />
    )
    return null
  }, [subModule, businessVersion, id, canEditFlag, modelKey])

  return renderContractType
}
export default observer(Index)
