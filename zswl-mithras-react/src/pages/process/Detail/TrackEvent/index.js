import { observer } from '@zswl/admin'
import { TrackEventDetail as LeaseTrack } from '@/components/TrackEvent/TrackEventEntries'

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
