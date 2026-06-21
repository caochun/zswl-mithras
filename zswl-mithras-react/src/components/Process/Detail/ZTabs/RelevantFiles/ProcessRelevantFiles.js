import { useMemo } from 'react'
import { Empty } from 'antd'
import { observer } from '@zswl/admin'
import ContractApplication from './Components/ContractApplication/ProcessRelevantContractApplicationFiles'
import PaymentApplication from './Components/PaymentApplication/ProcessRelevantPaymentApplicationFiles'
import ProjectReview from './Components/ProjectReview/ProcessRelevantProjectReviewFiles'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import styles from './index.less'

const ProcessRelevantFiles = () => {
  const { detailData, isNewLayout } = useFlowData()
  const { subModule, canEditFlag, mainModule } = detailData

  const commonProps = {
    isNewLayout,
    detailData,
    canEdit: canEditFlag,
  }

  const CurrentModule = useMemo(() => {
    const moduleMap = {
      PROJ_REVIEW: <ProjectReview {...commonProps} />,

      PAYMENT: <PaymentApplication {...commonProps} />,
      CONTRACT: <ContractApplication {...commonProps} />,
    }
    return moduleMap[mainModule]
  }, [subModule, mainModule])
  return (
    <div className={styles.keyInfo}>
      <div>{CurrentModule ?? <Empty></Empty>}</div>
    </div>
  )
}

export default observer(ProcessRelevantFiles)
