import EditDescription from '@/components/Table/EditDescription'
import { getDescColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { observer } from '@zswl/admin'

const Index = ({ canEditFlag = true, store }) => {
  const detail = store.page.getData()

  const columnName = ['风险信号及重大事项、风险防范措施', '查询分析及查询结论']
  const columns = getDescColumns(ALL_COLUMNS, columnName)
  const newDetail = {
    ...detail,
  }
  return (
    <EditDescription
      title={'查询总结'}
      style={{ marginTop: 20, marginBottom: 20 }}
      detail={newDetail}
      saveData={store.onSaveChaXun}
      canEdit={canEditFlag}
      columns={columns}
      labelStyle={{ background: '#F5F6FA', width: 320 }}
    />
  )
}

export default observer(Index)
