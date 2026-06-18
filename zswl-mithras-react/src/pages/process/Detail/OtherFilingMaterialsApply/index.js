import { FilingMaterialsOtherApply as OtherFilingMaterialsApplyFlowDetail } from '@/components/FilingMaterials/FilingMaterialsEntries'

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
