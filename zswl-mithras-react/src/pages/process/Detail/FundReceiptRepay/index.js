import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import FinancialPaymentDetail from '@/components/Financial/PaymentDetail'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, modelKey, curTaskActivityIds } = props
  const renderContractType = useMemo(() => {
    return (
      <FinancialPaymentDetail
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
