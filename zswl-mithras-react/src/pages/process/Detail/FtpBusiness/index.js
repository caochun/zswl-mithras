import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import FtpBusinessDetail from '@/pages/budget/pricing/business/detail/[id$]'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, tab } = props
  const renderContractType = useMemo(() => {
    return (
      <FtpBusinessDetail
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          subModule,
          businessVersion,
          tab,
        }}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(Index)
