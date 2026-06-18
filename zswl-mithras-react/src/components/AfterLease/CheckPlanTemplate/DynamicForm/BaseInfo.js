import { AmountEditable, DatePickerEditable } from '@/components/Format'
import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../../CheckPlanTemplateColumns'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

const nameColumns = [
  '计划名称',
  '客户名称',
  '行业',
  '风险敞口余额',
  '剩余本金（万元）',
  '检查形式',
  '合同列表',
  '检查填报时间',
  '本次租后截止时间',
  {
    title: '检查日期',
    requiredMark: true,
    editable: (val) =>
      DatePickerEditable(val, 'checkTime', {
        required: true,
      }),
  },
  '租后检查部门',
  '客户主办',
  '协查风控经理',
]

function Index({ detail, saveData, active, canEdit = true }) {
  const nameColumns2 = [
    '客户名称',
    [3, 4].includes(active) && '担保人名称',
    '业务部门',
    '客户主办',
    '检查时段',
    '检查形式',
    // 一般检查
    window.location.href.includes('commonTemplate')
      ? {
          title: '检查日期',
          requiredMark: true,
          editable: (val) =>
            DatePickerEditable(val, 'checkTime', {
              required: true,
            }),
        }
      : '检查日期',
    '主要受访人员',
    { title: '职务', rename: '受访人员职务' },
    '联系方式',
    '风险敞口余额',
    '剩余本金（万元）',
    '合同合计金额',
    '合同到期日',
  ].filter(Boolean)

  const columns = getDescColumns(ALL_COLUMNS, nameColumns)
  const columns2 = getDescColumns(ALL_COLUMNS, nameColumns2)
  // 切换模版，form 数据未清空
  return (
    <>
      {[1, 3, 4].includes(active) && (
        <EditDescription detail={detail} saveData={saveData} canEdit={canEdit} columns={columns2} />
      )}
      {[0, 2].includes(active) && (
        <EditDescription detail={detail} saveData={saveData} canEdit={canEdit} columns={columns} />
      )}
    </>
  )
}

export default observer(Index)
