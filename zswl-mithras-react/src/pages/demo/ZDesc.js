import EditTable from '@/components/Table/EditDescription'
import ALL_COLUMNS from './Column'
import store from './store'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import _ from 'lodash'

const nameColumns = [
  '融资机构',
  '收付款编号',
  '总授信额度（元）',
  '剩余授信额度（元）',
  '融资金额（元）',
  '利息总额（元）',
  '增信方式：担保',
  '保理手续费（元）',
  '开征许可证费（元）',
  '保证金金额（元）',
  '其他费用（元）',
  '备注',
  '资金经理',
  '所属部门',
  '部门负责人',
  '分管领导',
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function Index({ detail, saveData, isLog, canEdit = true, newProject }) {
  const initEdit = newProject === 'true'
  const baseInfoDetail = store.page.getData()
  return (
    <EditTable
      detail={baseInfoDetail?.newDetail}
      saveData={saveData}
      canEdit={canEdit}
      initEdit={initEdit}
      columns={columns}
    />
  )
}

export default observer(Index)
