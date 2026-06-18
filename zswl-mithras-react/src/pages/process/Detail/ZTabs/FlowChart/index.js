import { ProcessBpmnFlowChart as BpmnFlowChart } from '@/components/Process/ProcessEntries'
import { observer } from '@zswl/admin'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'

const Index = () => {
  const { detailData } = useFlowData()
  const { processInstanceId } = detailData
  return <BpmnFlowChart processInstanceId={processInstanceId} height={'100vh'} />
}

export default observer(Index)
