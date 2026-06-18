import { observer } from '@zswl/admin'
import { EditTable } from '@/components/Table'
import ALL_COLUMNS from '../PaymentColumns'
import { getTableColumns, hasPermission } from '@/utils'
import fundReceiptRepayBorrowingApi from '@/api/financial/fundReceiptRepayBorrowingApi'

const nameColumns = ['现金流编号', '期项', '本金（元）', '核销状态', '实际贷款时间', '备注']
const columns = getTableColumns(ALL_COLUMNS, nameColumns) //
const Index = ({
  id: receiptRepayId,
  businessVersion,
  canEdit,
  isFormApproval,
  detail,
  isDirect,
}) => {
  const getList = async (params) => {
    if (detail) return Promise.resolve({ list: detail })
    const listFunc = isFormApproval
      ? fundReceiptRepayBorrowingApi.postBorrowingListCompare
      : fundReceiptRepayBorrowingApi.postBorrowingList
    return await listFunc({
      ...params,
      receiptRepayId,
      version: isFormApproval ? businessVersion : undefined,
    })
  }
  const saveData = async (data) => {
    const res = await fundReceiptRepayBorrowingApi.postBorrowingModify(data)
  }
  return (
    <EditTable
      title={isDirect ? '资金流入' : '借款流入'}
      canEdit={canEdit && hasPermission('fundReceiptRepayBorrowingModify')}
      tableApi={getList}
      saveData={saveData}
      columns={columns}
    />
  )
}
export default observer(Index)
