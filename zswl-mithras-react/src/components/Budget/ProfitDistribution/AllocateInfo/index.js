
import { observer } from '@zswl/admin'
import { useEffect } from 'react'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'

const Index = ({
  projectDistributionId,
  source,
  editRef,
  store,
  canEdit
}) => {


  const bmnameColumns = ['部门分润比', '部门投放分配比'].filter(Boolean)

  const bmBaseInfo_columns = getDescColumns(
    ALL_COLUMNS({
      source,
    }),
    bmnameColumns
  )
  console.log('bmBaseInfo_columns', bmBaseInfo_columns)

  useEffect(() => {
    projectDistributionId && store.getAllocateInfoDetail()
  }, [projectDistributionId])

  return (
    <div style={{ marginTop: 16 }}>
      <EditDescription
        styleBtn={{ transform: 'translateY(16px)' }}
        ref={editRef}
        title={'分配信息'}
        saveData={store.saveDepAllocateInfoDetail}
        detail={store.allocateInfoDetail}
        canEdit={canEdit}
        initEdit={false}
        columns={bmBaseInfo_columns}
      />
    </div>
  )
}

export default observer(Index)
