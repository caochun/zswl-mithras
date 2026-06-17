import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components'

const Report = ({ id, canEdit = true }) => {
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'filename',
    },
    {
      title: '上传人',
      dataIndex: 'createByName',
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
    },
  ]

  const params = {
    mainId: id,
    moduleType: 'OVERDUE_COLLECTION_REDUCTION',
    materialsType: 'NEW_DEDUCTION_INTEREST',
  }
  return (
    <NoEnumFileTable
      title={'附件资料'}
      canEdit={canEdit}
      columns={columns}
      params={params}
      canEditItem={false}
    />
  )
}
export default observer(Report)
