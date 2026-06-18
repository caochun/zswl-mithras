import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { CreditEstablishDetail as CreateEstablish } from '@/components/Credit/CreditEntries'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion } = props
  const renderContractType = useMemo(() => {
    return (
      <CreateEstablish
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
