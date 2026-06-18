import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Maintain/Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

const nameColumns = [
  '项目名称',
  '项目编号',
  '业务类型',
  '租赁类型',
  '合同编号',
  '承租人',
  '风控行业分类',
  '项目主办',
  '项目协办',
  '业务部门',
  '业务部门负责人',
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function Index({ detail }) {
  return <EditDescription title="项目信息" detail={detail} canEdit={false} columns={columns} />
}

export default observer(Index)
