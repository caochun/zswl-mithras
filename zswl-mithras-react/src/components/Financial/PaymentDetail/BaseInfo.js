import { EditDescription as EditTable } from '@/components/Table'
import ALL_COLUMNS from '../PaymentColumns'
import { getDescColumns, hasPermission } from '@/utils'
import { observer } from '@zswl/admin'
import _ from 'lodash'

const nameColumns = [
  '融资机构',
  { title: '收付款编号', rename: '融资编号' },
  '总授信额度（元）',
  '剩余授信额度（元）',
  '融资金额（元）',
  '利息总额（元）',
  '增信方式：担保',

  '保证金金额（元）',
  '备注',
  { title: '资金经理', editable: false },
  '所属部门',
  '部门负责人',
  '分管领导',
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function FinancialPaymentDetailBaseInfo({ detail, saveData, isLog, canEdit = true, newProject, isDirect }) {
  const initEdit = newProject === 'true'
  return (
    <EditTable
      detail={detail}
      saveData={saveData}
      canEdit={canEdit && hasPermission('fundReceiptRepayBaseInfoModify')}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
    />
  )
}

export default observer(FinancialPaymentDetailBaseInfo)
