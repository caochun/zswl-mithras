import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { AfterLeaseExternalCheckDetail as ExternalDetail } from '@/components/AfterLease/CheckPlanExternalEntries'

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
