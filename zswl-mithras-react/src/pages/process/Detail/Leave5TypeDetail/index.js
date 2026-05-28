import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import Leave5TypeDetail from '@/pages/afterLease/level5Classify/detail/[id$]'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, modelKey, curTaskActivityIds } = props
  const renderContractType = useMemo(() => {
    if (modelKey === 'AssetClassifyReview') {
      return (
        <div>
          <Leave5TypeDetail
            params={{ id }}
            query={{
              canEditFlags: canEditFlag ? 'true' : 'false',
              businessVersion,
              modelKey,
            }}
          />
        </div>
      )
    }
    return null
  }, [subModule, businessVersion, id, canEditFlag, modelKey])

  return renderContractType
}
export default observer(Index)
