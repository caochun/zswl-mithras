import { observer, getQuery } from '@zswl/admin'
import ALL_COLUMNS from './Column'
import { getTableColumns } from '@/utils'
import { CRUDTable } from '@/components/Table'
import Api from '@/api/financial/fundApi'

const nameColumns = ['银行名称', '银行账号', '账户性质', '账户类别']
const columns = getTableColumns(ALL_COLUMNS, nameColumns)

const Index = ({ financingId, businessVersion, canEdit, detail }) => {
  const isFormApproval = getQuery('typeId') == 'approval'

  const getList = async (params) => {
    if (detail) return Promise.resolve({ list: detail })
    const listFunc = isFormApproval ? Api.postCollectionListCompare : Api.postCollectionList
    const res = await listFunc({ ...params, financingId, businessVersion })
    return Promise.resolve({ list: res })
  }
  const addApi = async (data, initialValues) => {
    if (initialValues) {
      const res = await Api.postCollectionModify({
        ...data,
        id: initialValues.id,
      })
      return res
    }
    const res = await Api.postCollectionCreate({
      ...data,
      financingId,
      version: businessVersion,
    })
    return res
  }
  const deleteApi = async (data) => {
    const res = await Api.postCollectionDelete({
      ...data,
      financingId,
      version: businessVersion,
    })
    return res
  }
  return (
    <CRUDTable
      title={'我司还款账户'}
      tableApi={getList}
      canEdit={canEdit}
      addApi={addApi}
      deleteApi={deleteApi}
      columns={columns}
    />
  )
}
export default observer(Index)
