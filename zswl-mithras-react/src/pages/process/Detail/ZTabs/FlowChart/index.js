import BpmnFlowChart from '@/components/BpmnFlowChart'
import { observer } from '@zswl/admin'
import { useFlowData } from '@/utils/processFlow'

const Index = () => {
  const { detailData } = useFlowData()
  const { processInstanceId } = detailData
  return <BpmnFlowChart processInstanceId={processInstanceId} height={'100vh'} />
}

export default observer(Index)
