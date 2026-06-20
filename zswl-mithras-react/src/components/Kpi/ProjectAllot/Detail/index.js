import { observer, getQuery } from '@zswl/admin'
import { useEffect, useMemo, useRef, useState } from 'react'
import { Button, Page } from '@zswl/components'
import DetailLayout from '@/components/DetailLayout'
import BaseInfo from '../BaseInfo/KpiProjectAllotBaseInfo'
import AllocateInfo from '../AllocateInfo'
import ExtarInfo from '../ExtarInfo/KpiProjectAllotExtarInfo'
import BeforeAllocateInfoModal from '../BeforeAllocateInfoModal/KpiProjectAllotBeforeAllocateInfoModal'
import allotApi from '@/api/kpi/projectAllot/allot'
import { message } from 'antd'
import { ProcessInfoModal } from '@/components/Process/ProcessInfoModalEntries'
import { isBusinesshead } from '@/utils'
import Store from './Store'

const Index = ({
  params: { id: projectDistributionId },
  query: { source, businessVersion, canEditFlags = true, modelKey, curTaskActivityIds, taskStatus },
}) => {
  const store = useMemo(() => new Store(), [])
  const { processInstanceId } = store

  // source:unDeal 未分配| deal 已分配 | adjust 调整 | transfer 移交
  const canEditFormSource = ['unDeal', 'adjust'].includes(source) // 增减

  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoRef = useRef()
  const allocateInfoRef = useRef()

  const commonProps = {
    projectDistributionId,
    source,
    modelKey,
    businessVersion,
    curTaskActivityIds,
    taskStatus,
    canEdit: canEditFlags && canEditFormSource,
    // canEdit: canEditFlags && canEditFormSource && isBusinesshead(baseData.contractBelongDeptId),
  }

  const submit = async () => {
    if (baseInfoRef.current?.baseEdit || allocateInfoRef.current?.baseEdit) {
      message.info('请先保存后，再提交审批！')
      return
    }
    await allotApi.postProjectdistributionSubmit({ projectDistributionId })
    message.success('提交成功')
  }

  useEffect(() => {
    projectDistributionId && store.getProcessInstanceId()
  }, [projectDistributionId])

  return (
    <Page store={store.page} params={commonProps}>
      <DetailLayout
        extra={[
          processInstanceId && <ProcessInfoModal processInstanceId={processInstanceId} />,
          !isFormApproval && commonProps.canEdit && (
            <Button type="primary" onClick={submit}>
              提交审批
            </Button>
          ),
        ]}
      >
        <BaseInfo {...commonProps} editRef={baseInfoRef} store={store}></BaseInfo>
        <AllocateInfo {...commonProps} editRef={allocateInfoRef} store={store}></AllocateInfo>
        <ExtarInfo {...commonProps} store={store}></ExtarInfo>
      </DetailLayout>
      <BeforeAllocateInfoModal store={store}></BeforeAllocateInfoModal>
    </Page>
  )
}

export default observer(Index)
