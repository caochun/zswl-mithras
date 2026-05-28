import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import ExternalDetail from '@/pages/afterLease/checkPlan/externalDetail/[id$]'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion } = props
  const renderContractType = useMemo(() => {
    return (
      <ExternalDetail
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          subModule,
          businessVersion,
        }}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(Index)
