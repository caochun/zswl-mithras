import { useMemo } from 'react'
import { Empty } from 'antd'
import { observer } from '@zswl/admin'
import ContractApplication from './Components/ContractApplication'
import PaymentApplication from './Components/PaymentApplication'
import ProjectReview from './Components/ProjectReview'
import { useFlowData } from '@/pages/process/Detail/Context'
import styles from './index.less'

const Index = () => {
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

export default observer(Index)
