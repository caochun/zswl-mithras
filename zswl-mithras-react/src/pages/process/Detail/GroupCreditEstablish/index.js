import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CreateEstablish from '@/pages/credit/establish/detail/[id$]'

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
