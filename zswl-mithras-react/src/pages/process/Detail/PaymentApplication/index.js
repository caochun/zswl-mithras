import PaymentApplicationDetail from '@/pages/cpm/paymentApplication/detail/[id$]'
import PaymentWriteOffDetail from '@/pages/cpm/paymentWriteOff/detail/[id$]'
import { observer } from '@zswl/admin'

const ProjectReview = (props) => {
  const {
    canEditFlag,
    businessVersion,
    taskActivityId,
    modelKey,
    processInstanceId,
    taskStatus,
    dynamicFormKeyList,
  } = props
  // PaymentActualDetailFlow
  // PaymentCreateFlow

  if (modelKey === 'PaymentCreateFlow') {
    return (
      <PaymentApplicationDetail
        query={{
          businessVersion,
          taskActivityId,
          modelKey,
          processInstanceId,
          taskStatus,
          canEditFlag: canEditFlag ? 'true' : 'false',
        }}
        {...props}
      />
    )
  } else if (['PaymentActualDetailFlow', 'PaymentReviewInAdvancedFlow'].includes(modelKey)) {
    return (
      <PaymentWriteOffDetail
        query={{
          businessVersion,
          taskActivityId,
          modelKey,
          processInstanceId,
          taskStatus,
          canEditFlag: canEditFlag ? 'true' : 'false',
          dynamicFormKeyList,
        }}
        {...props}
      />
    )
  }
  return null
}
export default observer(ProjectReview)
