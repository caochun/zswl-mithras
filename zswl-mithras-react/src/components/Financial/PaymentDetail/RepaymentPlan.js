import { observer } from '@zswl/admin'
import { EditTable } from '@/components/Table'
import ALL_COLUMNS from '../PaymentColumns'
import { getTableColumns } from '@/utils'
import fundReceiptRepayExpenseApi from '@/api/financial/fundReceiptRepayExpenseApi'

const nameColumns = [
  '现金流编号',
  '费用类型',
  '金额（元）',
  '累计支付金额（元）',
  '本月支付金额（元）',
  '核销状态',
  '备注',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const FinancialPaymentRepaymentPlan = ({ id: receiptRepayId, businessVersion, canEdit, isFormApproval, detail }) => {
  const geiList = async (params) => {
    if (detail) return Promise.resolve({ list: detail })
    const listFunc = isFormApproval
      ? fundReceiptRepayExpenseApi.postExpenseListCompare
      : fundReceiptRepayExpenseApi.postExpenseList
    return await listFunc({
      ...params,
      receiptRepayId,
      version: isFormApproval ? businessVersion : undefined,
    })
  }
  const saveData = async (data) => {
    const params = data.map(({ id, remark, payAmount }) => ({
      id,
      remark,
      payAmount: payAmount * 10000,
    }))
    await fundReceiptRepayExpenseApi.postExpenseModify(params)
  }
  return (
    <EditTable
      title={'费用一览表'}
      tableApi={geiList}
      saveData={saveData}
      canEdit={canEdit}
      columns={columns}
    />
  )
}
export default observer(FinancialPaymentRepaymentPlan)
