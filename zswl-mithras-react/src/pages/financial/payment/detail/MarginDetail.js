import { observer } from '@zswl/admin'
import { EditTable } from '@/components'
import ALL_COLUMNS from '@/components/Financial/PaymentColumns'
import { getTableColumns } from '@/utils'
import fundReceiptRepayCashDepositApi from '@/api/financial/fundReceiptRepayCashDepositApi'
import { MatchOptionColumn } from '@/components/Format'

const nameColumns = [
  '现金流编号',
  MatchOptionColumn({ title: '费用类型', dataIndex: 'depositCashFlowType', editable: false }),
  { title: '金额（元）', dataIndex: 'amount' },
  '核销状态',
  '本月支付金额',
  '本月收入金额',
  '备注',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const Index = ({ id: receiptRepayId, businessVersion, canEdit, detail, isFormApproval }) => {
  const getList = async (params) => {
    if (detail) return Promise.resolve({ list: detail })
    const listFunc = isFormApproval
      ? fundReceiptRepayCashDepositApi.postDepositListCompare
      : fundReceiptRepayCashDepositApi.postDepositList
    return await listFunc({
      ...params,
      receiptRepayId,
      version: isFormApproval ? businessVersion : undefined,
    })
  }
  const saveData = async (data) => {
    const params = data.map((item) => {
      const { id, paidAmount, receiptAmount, remark } = item
      return {
        id,
        paidAmount: paidAmount * 10000,
        receiptAmount: receiptAmount * 10000,
        remark,
      }
    })

    await fundReceiptRepayCashDepositApi.postDepositModify(params)
  }
  return (
    <EditTable
      title={'保证金明细'}
      canEdit={canEdit}
      tableApi={getList}
      saveData={saveData}
      columns={columns}
    />
  )
}
export default observer(Index)
