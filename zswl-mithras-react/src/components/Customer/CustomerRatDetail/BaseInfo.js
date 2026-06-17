import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/pages/customer/customerRat/Columns'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

const nameColumns = [
  '客户名称',
  '客户编号',
  '成立日期',
  '注册资本',
  '行业分类',
  '风控行业分类',
  '所在省份',
  '所在城市',
  '所在区县',
  '业务范围',
  '项目经理',
  '所属部门',
]

const nameColumns2 = [
  '客户名称',
  '客户编号',
  '项目经理',
  '所属部门',
]

function Index({ detail, isLog, canEdit = true, initEdit }) {
  const columns = getDescColumns(ALL_COLUMNS, detail.hymxFlag ? nameColumns2 : nameColumns)
  return <EditDescription detail={detail} canEdit={false} columns={columns} />
}

export default observer(Index)
