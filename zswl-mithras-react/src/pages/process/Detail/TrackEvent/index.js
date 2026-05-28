import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import LeaseTrack from '@/pages/lease/tracking/detail/[id$]'

const Index = (props) => {
  const { id, businessVersion, modelKey, taskActivityId, taskStatus } = props

  return (
    <div>
      <LeaseTrack
        params={{ id }}
        query={{
          modelKey,
          businessVersion,
          taskActivityId,
          taskStatus,
          canEditFlags: 'true',
        }}
      />
    </div>
  )

  return renderContractType
}
export default observer(Index)
