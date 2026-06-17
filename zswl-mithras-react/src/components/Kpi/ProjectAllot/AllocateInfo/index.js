import EditDescription from '@/components/Table/EditDescription'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import ALL_COLUMNS from '@/components/Kpi/ProjectAllot/Column'
import { Button } from '@zswl/components'
import { getDescColumns } from '@/utils'
import BeforeInfo from './BeforeInfo'

const Index = ({
  canEdit,
  projectDistributionId,
  source,
  editRef,
  curTaskActivityIds,
  taskStatus,
  store,
  businessVersion,
  modelKey,
}) => {
  const isShowBeforeData = [
    'KpiProjectDistributionModifyFlow',
    'KpiProjectDistributionTransferFlow',
  ].includes(modelKey)
  const nameColumns = [
    { title: '分润比', rename: '人员分润比' },
    source !== 'unDeal' && '生效月份',
  ].filter(Boolean)
  const bmnameColumns = ['部门分润比', '部门投放分配比'].filter(Boolean)
  const baseInfo_columns = getDescColumns(
    ALL_COLUMNS({
      source,
    }),
    nameColumns
  )
  const bmBaseInfo_columns = getDescColumns(
    ALL_COLUMNS({
      source,
    }),
    bmnameColumns
  )

  useEffect(() => {
    projectDistributionId && store.getAllocateInfoDetail()
  }, [projectDistributionId])

  return (
    <div>
      <div style={{ fontWeight: 'bold', fontSize: 16 }}>分配信息</div>
      <EditDescription
        ref={editRef}
        title={<h5 style={{ margin: '12px 0 0 0' }}>本次分配信息</h5>}
        saveData={store.saveAllocateInfoDetail}
        detail={store.allocateInfoDetail}
        canEdit={canEdit || (curTaskActivityIds === 'userTask_teamLeader' && taskStatus === '1')}
        initEdit={false}
        columns={baseInfo_columns}
      />
      {isShowBeforeData && (
        <BeforeInfo
          source={source}
          listName="weightInfoList"
          projectDistributionId={projectDistributionId}
          businessVersion={businessVersion}
        />
      )}
      <EditDescription
        styleBtn={{ transform: 'translateY(16px)' }}
        ref={editRef}
        title={<h5 style={{ margin: '12px 0 0 0' }}>本次分配信息</h5>}
        saveData={store.saveDepAllocateInfoDetail}
        detail={store.allocateInfoDetail}
        canEdit={canEdit || (curTaskActivityIds === 'userTask_teamLeader' && taskStatus === '1')}
        initEdit={false}
        columns={bmBaseInfo_columns}
      />
      {isShowBeforeData && (
        <BeforeInfo
          source={source}
          listName="deptWeightInfoList"
          projectDistributionId={projectDistributionId}
          businessVersion={businessVersion}
        />
      )}
    </div>
  )
}

export default observer(Index)
