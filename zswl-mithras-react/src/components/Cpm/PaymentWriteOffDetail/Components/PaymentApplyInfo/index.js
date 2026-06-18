import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useRef, useEffect, useState } from 'react'

function Index({ detail, saveData, canEdit = false }) {
  const isZhiZu = detail.leaseTypeCode === 'zhi_zu'
  const columns = getDescColumns(ALL_COLUMNS({ isZhiZu }))
  // 出纳节点
  const ref = useRef()

  return (
    <EditDescription
      style={{ marginTop: 20 }}
      title="付款申请"
      detail={detail}
      saveData={saveData}
      canEdit={canEdit}
      columns={columns}
      ref={ref}
      column={2}
      contentStyle={{
        width: 200,
      }}
    />
  )
}

export default observer(Index)
