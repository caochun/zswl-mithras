import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { AfterLeaseLevel5Classify as Leave5Type } from '@/components/AfterLease/Level5ClassifyEntries'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, modelKey, curTaskActivityIds, taskStatus } =
    props
  const renderContractType = useMemo(() => {
    if (
      [
        'AssetClassifyReviewFlow',
        'AssetClassifyReviewMeetingFlow',
        'AssetClassifyRiskMeetingFlow',
        'AssetClassifyBoardMeetingFlow',
      ].includes(modelKey)
    ) {
      return (
        <div>
          <Leave5Type
            params={{ id }}
            query={{
              curTaskActivityIds,
              modelKey,
              businessVersion,
              taskStatus,
              canEditFlags: canEditFlag ? 'true' : 'false',
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
