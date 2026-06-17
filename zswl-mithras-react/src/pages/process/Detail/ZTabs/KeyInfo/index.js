import { useMemo } from 'react'
import { observer, ErrorBoundary } from '@zswl/admin'
import { Collapse } from '@/components'
import { Tag } from 'antd'
import ContractApplication from './Components/ContractApplication'
import ImportantResolutions from './ImportantResolutions'
import PaymentApplication from './Components/PaymentApplication'
import ProjectReview from './Components/ProjectReview'
import ProjectPricing from './Components/ProjectPricing'
import { useFlowData } from '@/utils/processFlow'
import ChangeReason from './ChangeReason'
import styles from './index.less'
import BlankBlock from '@/components/Process/BlankBlock'
import ApprovalHistory from '@/components/Process/ApprovalHistory'

import CustomerRat from './Components/CustomerRat'

// 已经开发了“关键信息”的流程且有“重要文件”模块的流程 modelKey
const flowModelKey = {
  CONTRACT: [
    'ContractCreateFlow',
    'ContractModifyFlow',
    'ContractEarlySettleFlow',
    'ContractChangeRepayPlanFlow',
  ],
  PAYMENT: ['PaymentCreateFlow'],
}

const Index = () => {
  const { detailData, hasKeyInfo } = useFlowData()
  const { subModule, modelKey, mainModule, businessKey, processInstanceId } = detailData

  // 是否有重要文件模块
  const hasImportantResolutions = flowModelKey[mainModule]
    ? flowModelKey[mainModule].includes(modelKey)
    : hasKeyInfo

  const CurrentModule = useMemo(() => {
    const moduleMap = {
      PROJ_REVIEW: <ProjectReview />,
      PROJ_PRICING: <ProjectPricing />,
      PAYMENT: <PaymentApplication />,
      CONTRACT: <ContractApplication />,
    }
    return moduleMap[mainModule]
  }, [subModule, mainModule, businessKey])
  return (
    <ErrorBoundary fallback={<Tag>渲染出错了</Tag>}>
      <div className={styles.keyInfo}>
        {/* 变更说明 */}
        <ChangeReason></ChangeReason>
        {/* 关键信息 */}
        {hasKeyInfo && <Collapse header={'关键信息'}>{CurrentModule}</Collapse>}
        {/* 重要文件 */}
        {hasImportantResolutions && <ImportantResolutions />}
        {/* 审批历史 */}
        <BlankBlock></BlankBlock>
        <Collapse header={'审批记录'}>
          <ApprovalHistory processInstanceId={processInstanceId} />
        </Collapse>
      </div>
    </ErrorBoundary>
  )
}

export default observer(Index)
