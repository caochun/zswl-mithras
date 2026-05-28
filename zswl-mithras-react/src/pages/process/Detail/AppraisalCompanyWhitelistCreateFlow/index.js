import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import WhiteList from '@/pages/whiteList/detail/[id$]'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, type } = props
  const renderContractType = useMemo(() => {
    return (
      <WhiteList
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          businessVersion,
          type,
        }}
      />
    )
  }, [subModule, businessVersion, canEditFlag])

  return renderContractType
}
export default observer(Index)
