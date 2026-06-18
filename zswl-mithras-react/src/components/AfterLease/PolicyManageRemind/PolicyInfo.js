import { observer } from '@zswl/admin'
import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/components/PolicyColumns'
import { getDescColumns } from '@/utils'

const nameColumns = [
  '保险单号',
  '保单金额(元)',
  '保险机构',
  '险种',
  { title: '保险起始日-详情', rename: '保险起始日' },
  { title: '保险到期日-详情', rename: '保险到期日' },
  '是否续保',
  '标识信息',
  {
    title: '备注',
    requiredMark: false,
  },
]

function Index({ detail }) {
  const columns = getDescColumns(ALL_COLUMNS, nameColumns)

  return <EditDescription detail={detail} canEdit={false} columns={columns} title="原保单信息" />
}

export default observer(Index)
