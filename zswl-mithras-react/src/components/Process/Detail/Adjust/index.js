import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { AfterLeaseAdjustDetail as Adjust } from '@/components/AfterLease/AdjustDetailEntries'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion } = props
  const renderContractType = useMemo(() => {
    return (
      <Adjust
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          subModule,
          businessVersion,
        }}
      />
    )
  }, [subModule, businessVersion, canEditFlag])

  return renderContractType
}
export default observer(Index)
