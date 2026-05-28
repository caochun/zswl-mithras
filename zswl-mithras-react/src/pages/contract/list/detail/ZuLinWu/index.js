import { observer } from '@zswl/admin'
import Lease from './Lease'

const Index = ({ baseStore, canEditFlag = true, flowId, isLog, taskStatus, taskActivityId }) => {
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()

  return (
    <Lease
      baseStore={baseStore}
      isFormApproval={isFormApproval}
      contractId={contractId}
      businessVersion={businessVersion}
      flowId={flowId}
      canEditFlag={canEditFlag}
      isLog={isLog}
      taskStatus={taskStatus}
      taskActivityId={taskActivityId}
    ></Lease>
  )
}

export default observer(Index)
