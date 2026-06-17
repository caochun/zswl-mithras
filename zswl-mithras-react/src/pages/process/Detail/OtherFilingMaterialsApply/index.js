import OtherFilingMaterialsApplyFlowDetail from '@/components/FilingMaterials/OtherFilingMaterialsApply'

const OtherFilingMaterialsApply = ({ id, taskActivityId, curTaskActivityIds }) => {
  return (
    <OtherFilingMaterialsApplyFlowDetail
      id={id}
      taskActivityId={taskActivityId}
      curTaskActivityIds={curTaskActivityIds}
    />
  )
}

export default OtherFilingMaterialsApply
