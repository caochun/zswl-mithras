import OtherFilingMaterialsApplyFlowDetail from '@/pages/fillingMaterialsDetail/otherFillingMaterialsDetail/index'

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