import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { CustomerRatDetail as CustomerRat } from '@/components/Customer/CustomerRatingDetailEntries'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, taskActivityId, taskId, modelCode } = props
  const renderContractType = useMemo(() => {
    return (
      <CustomerRat
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          canApproval: taskActivityId === 'userTask_riskManager' ? 'true' : 'false',
          subModule,
          taskId,
          businessVersion,
          model: modelCode,
        }}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(Index)
