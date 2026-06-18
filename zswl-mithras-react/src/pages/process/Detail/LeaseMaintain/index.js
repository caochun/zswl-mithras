import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import LeaseMaintain from '@/components/Lease/MaintainDetail'

const Index = (props) => {
  const { id, businessVersion, modelKey, curTab, taskActivityId, startUserId, taskStatus } = props

  const renderContractType = useMemo(() => {
    if (['LeaseModifyFlow', 'LeaseCreateFlow'].includes(modelKey)) {
      return (
        <div>
          <LeaseMaintain
            params={{ id }}
            query={{
              modelKey,
              businessVersion,
              taskActivityId,
              taskStatus,
              startUserId,
              canEdit: curTab === 'pending',
            }}
          />
        </div>
      )
    }

    return null
  }, [businessVersion, id, modelKey, taskStatus, curTab])

  return renderContractType
}
export default observer(Index)
