import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { RiskPublicMonitorDetail as MonitorEarly } from '@/components/Risk/PublicMonitorDetailEntries'
import PublicMonitor from '../PublicMonitor'

const Index = (props) => {
  const {
    canEditFlag,
    subModule,
    id,
    businessVersion,
    taskActivityId,
    modelKey,
    processInstanceId,
    businessKey,
  } = props
  console.log('props: ', props)
  const isWarning = ['RiskControlWarnNotPaymentFlow', 'RiskControlWarnPaymentFlow'].includes(
    modelKey
  )
  const isNotPaymentFlow = ['RiskControlNotPaymentFlow'].includes(modelKey)
  const canEditFlags = canEditFlag ? 'true' : 'false'

  if (isWarning) {
    return (
      <MonitorEarly
        params={{ id }}
        query={{
          canEditFlags,
          subModule,
          businessVersion,
          taskActivityId,
          processInstanceId,
        }}
      />
    )
  }

  return <PublicMonitor {...props} />
}
export default observer(Index)
