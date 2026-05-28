import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import DebtRat from '@/pages/customer/debtRat/detail/[id$]'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, taskActivityId } = props
  const renderContractType = useMemo(() => {
    return (
      <DebtRat
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          canApproval: taskActivityId === 'userTask_riskManager' ? 'true' : 'false',
          subModule,
          businessVersion,
        }}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(Index)
