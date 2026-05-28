import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

const nameColumns = ['客户名称', '风险敞口', '逾期租金', '逾期罚息', '当前最大逾期天数']

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function Index({ dataSource, saveData, canEdit = true, newProject }) {
  const initEdit = newProject === 'true'
  return (
    <EditDescription
      detail={dataSource}
      saveData={saveData}
      canEdit={false}
      initEdit={false}
      columns={columns}
    />
  )
}

export default observer(Index)
