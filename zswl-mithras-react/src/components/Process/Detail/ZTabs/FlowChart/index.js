import BpmnFlowChart from '../../../BpmnFlowChart'
import { observer } from '@zswl/admin'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'

const ProcessFlowChart = () => {
  const { detailData } = useFlowData()
  const { processInstanceId } = detailData
  return <BpmnFlowChart processInstanceId={processInstanceId} height={'100vh'} />
}

export default observer(ProcessFlowChart)
