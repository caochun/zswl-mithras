import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components/Table'
import Api from '@/api/customer/applyPermissionFileApi'

const Report = ({ id, canEdit = true, store }) => {
  const { batchNo, processInstanceId } = store.page.getParams()
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
    moduleType: 'CLIENT',
    materialsType: 'CLIENT_AUTHORITY',
    batchNo,
    processInstanceId,
  }
  return (
    <NoEnumFileTable
      title={'补充资料'}
      canEdit={canEdit}
      columns={columns}
      params={params}
      uploadApi={(data) => Api.fileUpload({ ...data, ...params })}
      tableApi={() => Api.fileList({ ...params })}
      canEditItem={false}
    />
  )
}
export default observer(Report)
