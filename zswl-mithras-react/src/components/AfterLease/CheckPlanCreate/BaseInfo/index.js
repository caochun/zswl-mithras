import EditDescription from '@/components/Table/EditDescription'
import { getDescColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { observer } from '@zswl/admin'
import moment from 'moment'

const Index = ({ detail, canEditFlag = true, saveData }) => {
  const columnName = [
    '计划名称',
    '计划类型',
    detail?.planType === 'QUARTER'
      ? {
          title: '检查填报时间',
          rename: '本次租后截止时间',
        }
      : '检查填报时间',
    ,
    detail?.planType === 'QUARTER'
      ? {
          title: '检查所属时间-季度',
          rename: '检查所属时间',
        }
      : {
          title: '检查所属时间-其他',
          rename: '检查所属时间',
        },
  ]
  const columns = getDescColumns(ALL_COLUMNS, columnName)
  const { year, quarter, month, checkStartDate, checkEndDate } = detail
  const newDetail = {
    ...detail,
    checkDate: checkStartDate ? [moment(checkStartDate), moment(checkEndDate)] : undefined,
    quarter: quarter ? moment().year(year).quarter(quarter) : undefined,
    month: month ? moment(`${year}/${month}`, 'yyyy/MM') : undefined,
  }
  return (
    <EditDescription
      title={'基本信息'}
      style={{ marginTop: 20, marginBottom: 20 }}
      detail={newDetail}
      saveData={saveData}
      canEdit={canEditFlag}
      columns={columns}
    />
  )
}

export default observer(Index)
