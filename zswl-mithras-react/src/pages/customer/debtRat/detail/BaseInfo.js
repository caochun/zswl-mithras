import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Columns'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

const nameColumns = [
  '项目编号',
  '项目名称',
  '承租人',
  '租赁类型',
  '评估主体名称',
  '评估主体区域',
  '评估主体营业收入',
  '地区分类',
  '行业分类',
  '资金用途',
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function Index({ detail, isLog, canEdit = true, initEdit }) {
  return <EditDescription detail={detail} canEdit={false} columns={columns} />
}

export default observer(Index)
