import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useEffect, useRef } from 'react'

function Index({ store, canEdit, isFormApproval }) {
  const editRef = useRef()
  const columns = getDescColumns(ALL_COLUMNS)
  const detail = store.page.getData()
  useEffect(() => {
    store.editRef = editRef
  })

  return (
    <EditDescription
      ref={editRef}
      title="基本信息"
      saveData={store.saveData}
      // initEdit={canEdit}
      detail={detail}
      canEdit={canEdit}
      columns={columns}
    />
  )
}

export default observer(Index)
