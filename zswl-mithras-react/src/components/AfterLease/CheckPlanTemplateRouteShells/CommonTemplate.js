import Template from '../CheckPlanTemplate/AfterLeaseCheckPlanTemplate'
import { Page } from '@zswl/components'

// 一般检查、为了解决面包屑问题
const AfterLeaseCommonCheckPlanTemplateShell = ({ params: id }) => {
  return (
    <Page>
      <Template params={id}></Template>
    </Page>
  )
}

export default AfterLeaseCommonCheckPlanTemplateShell
