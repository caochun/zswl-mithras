import { getQuery, observer } from '@zswl/admin'
import ALL_COLUMNS from './Column'
import { getTableColumns } from '@/utils'
import { CRUDTable } from '@/components/Table'
import fundReceiptAccountApi from '@/api/financial/fundReceiptAccountApi'
import { useCallback } from 'react'

const nameColumns = ['客户名称', '账户名称', '银行账号', '开户行']
const columns = getTableColumns(ALL_COLUMNS, nameColumns) //
const Index = ({ id: receiptRepayId, businessVersion, canEdit, detail }) => {
  const isFormApproval = getQuery('typeId') == 'approval'
  const getList = useCallback(
    async (params) => {
      if (detail) return Promise.resolve({ list: detail })
      const listFunc = isFormApproval
        ? fundReceiptAccountApi.postAccountListCompare
        : fundReceiptAccountApi.postAccountList
      return await listFunc({ ...params, receiptRepayId, version: businessVersion })
    },
    [detail, isFormApproval, receiptRepayId, businessVersion]
  )
  const addApi = async (data, initialValues = {}) => {
    const { id } = initialValues
    if (id) {
      const res = await fundReceiptAccountApi.postAccountModify({
        ...data,
        receiptRepayId,
        id,
      })
      return res
    }
    const res = await fundReceiptAccountApi.postAccountAdd({
      ...data,
      receiptRepayId,
      version: businessVersion,
    })
    return res
  }
  const deleteApi = async (data) => {
    const res = await fundReceiptAccountApi.postAccountRemove({
      ...data,
      receiptRepayId,
      version: businessVersion,
    })
    return res
  }
  return (
    <CRUDTable
      title={'付款方收款账户'}
      tableApi={getList}
      canEdit={canEdit}
      addApi={addApi}
      deleteApi={deleteApi}
      columns={columns}
    />
  )
}
export default observer(Index)
