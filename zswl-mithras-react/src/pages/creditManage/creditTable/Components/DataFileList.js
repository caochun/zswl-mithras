import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components'
import commonApi from '@/api/common/fileList'
import { getUserInfo } from '@/utils'

const Report = ({ canEdit = true, batchNo }) => {
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
    mainId: batchNo,
    moduleType: 'CREDIT_REPORT',
    // materialsType: 'OTHER',
  }
  return (
    <NoEnumFileTable
      title={'资料文件'}
      canEdit={true}
      columns={columns}
      params={params}
      canEditItem={false}
      canDelete={(record) => {
        return (record.createBy?.value || record.createBy) == getUserInfo().id
      }}
    />
  )
}
export default observer(Report)
