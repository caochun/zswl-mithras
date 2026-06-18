
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

function Index({ detail, saveData, isLog, canEdit = true, initEdit }) {
  const nameColumns = [
    '到期未付租金(元)',
    '违约金(元)',
    '未到期本金(元)',
    {
      title: '原到期日',
      editable: false,
    },
    '保证金余额(元)',
    '保证金是否内扣',
    '名义价款(元)',
    '合计金额(元)',
  ]
  const columns = getDescColumns(ALL_COLUMNS({ detail }), nameColumns)
  return (
    <EditDescription
      title="结清方案"
      detail={detail}
      saveData={saveData}
      canEdit={canEdit}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
    />
  )
}

export default observer(Index)
