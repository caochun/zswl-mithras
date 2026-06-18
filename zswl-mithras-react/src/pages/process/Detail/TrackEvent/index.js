import { observer } from '@zswl/admin'
import { TrackingDetail as LeaseTrack } from '@/components/TrackEvent/TrackingEntries'

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
}
export default observer(Index)
