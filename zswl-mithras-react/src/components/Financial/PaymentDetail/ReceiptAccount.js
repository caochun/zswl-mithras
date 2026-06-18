import { getQuery, observer } from '@zswl/admin'
import ALL_COLUMNS from '../PaymentColumns'
import { getTableColumns, hasPermission } from '@/utils'
import CRUDTable from '@/components/Table/CRUDTable'
import fundReceiptAccountApi from '@/api/financial/fundReceiptAccountApi'

const nameColumns = ['客户名称', '账户名称', '银行账号', '开户行']
const columns = getTableColumns(ALL_COLUMNS, nameColumns) //
const Index = ({ id: receiptRepayId, businessVersion, canEdit, detail, isFormApproval }) => {
  const version = isFormApproval ? businessVersion : undefined
  const getList = async (params) => {
    if (detail) return Promise.resolve({ list: detail })
    const listFunc = isFormApproval
      ? fundReceiptAccountApi.postAccountListCompare
      : fundReceiptAccountApi.postAccountList
    return await listFunc({
      ...params,
      receiptRepayId,
      version,
    })
  }
  const addApi = async (data, initialValues = {}) => {
    const { id } = initialValues

    const newParams = Object.fromEntries(
      Object.entries(data).map(([key, val]) => [key, val || ' '])
    )
    const func = id ? fundReceiptAccountApi.postAccountModify : fundReceiptAccountApi.postAccountAdd
    const params = { ...newParams, receiptRepayId, id, version }
    return await func(params)
  }
  const deleteApi = async (data) => {
    const res = await fundReceiptAccountApi.postAccountRemove({
      ...data,
      receiptRepayId,
      version,
    })
    return res
  }
  return (
    <CRUDTable
      title={'对方收款账户'}
      tableApi={getList}
      canEdit={canEdit && hasPermission('fundReceiptAccountAdd')}
      addApi={addApi}
      deleteApi={deleteApi}
      columns={columns}
    />
  )
}
export default observer(Index)
