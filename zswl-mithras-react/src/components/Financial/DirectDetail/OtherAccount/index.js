import { observer, getQuery } from '@zswl/admin'
import { CRUDTable } from '@/components/Table'
import Api from '@/api/financial/fundApi'

const columns = [
  // { title: '客户名称', dataIndex: 'clientName', rules: [{ required: true }] },
  {
    title: '账户名称',
    dataIndex: 'accountName',
    rules: [{ required: true }],
    width: 200,
  },
  { title: '银行账号', dataIndex: 'accountNum', rules: [{ required: true }] },
  { title: '开户行', dataIndex: 'accountAddress', rules: [{ required: true }] },
]

const FinancialDirectDetailOtherAccount = ({ id: financingId, businessVersion, canEdit, detail }) => {
  const isFormApproval = getQuery('typeId') == 'approval'

  const getList = async (params) => {
    if (detail) return Promise.resolve({ list: detail })
    const listFunc = Api.postDirectCollectionList
    const res = await listFunc({ ...params, financingId, businessVersion })
    return Promise.resolve({ list: res })
  }
  const addApi = async (data, initialValues) => {
    if (initialValues) {
      const res = await Api.postDirectCollectionModify({
        ...data,
        id: initialValues.id,
      })
      return res
    }
    const res = await Api.postDirectCollectionCreate({
      ...data,
      financingId,
      version: businessVersion,
    })
    return res
  }
  const deleteApi = async (data) => {
    const res = await Api.postDirectCollectionDelete({
      ...data,
      financingId,
      version: businessVersion,
    })
    return res
  }
  return (
    <CRUDTable
      title={'对方收款账户'}
      tableApi={getList}
      canEdit={canEdit}
      addApi={addApi}
      deleteApi={deleteApi}
      columns={columns}
    />
  )
}
export default observer(FinancialDirectDetailOtherAccount)
