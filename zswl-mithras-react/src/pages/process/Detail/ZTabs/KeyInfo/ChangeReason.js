import { Collapse } from '@/components'
import { useEffect, useMemo, useState } from 'react'
import { Tag } from 'antd'
import { observer, ErrorBoundary } from '@zswl/admin'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import { ProcessBlankBlock as BlankBlock } from '@/components/Process/ProcessEntries'
import { ApprovalDetail } from '@/components/Table'
import processModifyRemarkApi from '@/api/common/approvalRemarkApi'
import styles from './index.less'

// 变更说明配置
const approvalParamsConf = {
  ProjReviewModifyFlow: {
    approvalParams: {
      moduleType: 'PROJ_REVIEW',
      remarkType: 'MODIFY',
    },
    reconsiderParams: {
      moduleType: 'PROJ_REVIEW',
      remarkType: 'RECONSIDER',
    },
    functionCode: 'processmodifyremarkallprojreview',
  },
  ProjReviewPricingModifyApprovalFlow: {
    approvalParams: {
      moduleType: 'PROJ_REVIEW',
      remarkType: 'MODIFY',
    },
    reconsiderParams: {
      moduleType: 'PROJ_REVIEW',
      remarkType: 'RECONSIDER',
    },
    functionCode: 'processmodifyremarkallprojreview',
  },
  ContractModifyFlow: {
    approvalParams: {
      moduleType: 'CONTRACT',
      remarkType: 'MODIFY',
    },
    functionCode: 'processmodifyremarkallcontract',
  },
}

const Index = () => {
  const { detailData } = useFlowData()
  const { modelKey, businessKey } = detailData
  const [approvalDetail, setApprovalDetail] = useState({})
  const [reconsiderDetail, setReconsiderDetail] = useState({})

  const currentModel = approvalParamsConf[modelKey]
  if (!currentModel) return null

  const { approvalParams, reconsiderParams, functionCode } = currentModel ?? {}

  const getData = async () => {
    if (reconsiderParams) {
      const result = await Promise.all([
        processModifyRemarkApi.postRemarkAll(
          { ...approvalParams, mainId: businessKey },
          functionCode
        ),
        processModifyRemarkApi.postRemarkAll(
          { ...reconsiderParams, mainId: businessKey },
          functionCode
        ),
      ])
      setApprovalDetail(result[0])
      setReconsiderDetail(result[1])
    } else {
      const result = await processModifyRemarkApi.postRemarkAll(
        { ...approvalParams, mainId: businessKey },
        functionCode
      )
      setApprovalDetail(result)
    }
  }

  useEffect(() => {
    getData()
  }, [businessKey])

  return (
    <ErrorBoundary fallback={<Tag>渲染出错了</Tag>}>
      <BlankBlock></BlankBlock>
      <div className={styles.keyInfo}>
        <Collapse header={'变更说明'}>
          <ApprovalDetail data={approvalDetail} hasTitle={false} showLast />
          <ApprovalDetail data={reconsiderDetail} hasTitle={false} showLast />
        </Collapse>
      </div>
      <BlankBlock></BlankBlock>
    </ErrorBoundary>
  )
}

export default observer(Index)
