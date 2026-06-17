import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import FinanceOverdue from '@/components/Budget/AccountsReceivableDetail'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, tab, processInstanceId } = props
  const renderContractType = useMemo(() => {
    return (
      <FinanceOverdue
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          subModule,
          businessVersion,
          processInstanceId,
          tab,
        }}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(Index)
