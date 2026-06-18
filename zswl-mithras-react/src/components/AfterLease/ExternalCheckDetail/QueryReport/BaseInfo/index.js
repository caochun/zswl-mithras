import { EditDescription } from '@/components/Table'
import { getDescColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { observer } from '@zswl/admin'
import moment from 'moment'

const Index = ({ canEditFlag = true, store }) => {
  const detail = store.page.getData()

  const columnName = [
    '租后检查部门',
    '客户主办',
    '客户名称',
    '检查日期',
    '行业',
    '风险敞口余额',
    '合同金额',
    '合同最终到期日',
    '下次还款日期',
    '下次还款金额',
  ]
  const columns = getDescColumns(ALL_COLUMNS, columnName)
  const newDetail = {
    ...detail,
    inspectionDate: detail.inspectionDate ? moment(detail.inspectionDate) : undefined,
  }
  return (
    <EditDescription
      title={'基本信息'}
      style={{ marginTop: 20, marginBottom: 20 }}
      detail={newDetail}
      saveData={store.onSaveBaseInfo}
      canEdit={canEditFlag}
      columns={columns}
    />
  )
}

export default observer(Index)
