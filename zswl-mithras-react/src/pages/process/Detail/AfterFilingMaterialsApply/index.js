import AfterFilingMaterialsApplyFlowDetail from '@/components/FilingMaterials/AfterFilingMaterialsApply'

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
