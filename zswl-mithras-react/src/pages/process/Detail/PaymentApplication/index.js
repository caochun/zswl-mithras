import PaymentApplicationDetail from '@/components/Cpm/PaymentApplicationDetail'
import PaymentWriteOffDetail from '@/components/Cpm/PaymentWriteOffDetail'
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
