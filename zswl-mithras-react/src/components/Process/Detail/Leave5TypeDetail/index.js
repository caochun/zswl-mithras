import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { AfterLeaseLevel5ClassifyDetail as Leave5TypeDetail } from '@/components/AfterLease/Level5ClassifyDetailEntries'

const ProcessDetailLeave5TypeDetail = (props) => {
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
export default observer(ProcessDetailLeave5TypeDetail)
