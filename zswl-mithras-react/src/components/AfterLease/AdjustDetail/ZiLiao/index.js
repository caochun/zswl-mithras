import { observer } from '@zswl/admin'
import Api from '@/api/afterLease/adjustReportApi'
import { FileTable } from '@/components/Table'

const MODULE_TYPE = 'ADJUST'
const Report = ({ adjustId: mainId, businessVersion, canEditFlag: canEdit = true }) => {
  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    businessVersion,
  }
  return (
    <FileTable
      enumType={'afterLeaseAdjustMaterialsEnum'}
      uploadApi={({ file, fileType: materialsType }) =>
        Api.uploadReport({
          file,
          adjustId: mainId,
          materialsType,
        })
      }
      params={params}
      title={<h4>项目调整资料</h4>}
      canEdit={canEdit}
      columns={columns}
    />
  )
}
export default observer(Report)
