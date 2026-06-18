import { FilingMaterialsAfterApply as AfterFilingMaterialsApplyFlowDetail } from '@/components/FilingMaterials/AfterApplyEntries'

const AfterFilingMaterialsApply = ({ id, canEditFlag, taskActivityId }) => {
  return (
    <AfterFilingMaterialsApplyFlowDetail
      id={id}
      canEditFlag={canEditFlag}
      taskActivityId={taskActivityId}
    />
  )
}

export default AfterFilingMaterialsApply
