import { observer } from '@zswl/admin'
import Api from './api'
import { NoEnumFileTable } from '@/components/Table'

const Report = ({ id, canEdit = true, businessVersion, title }) => {
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
    },
  ]

  const params = {
    mainId: id,
    moduleType: 'CONTRACT',
    materialsType: 'CONTRACT_SETTLE',
    ext: {
      queryType: 'SETTLE',
    },
    businessVersion,
  }
  return (
    <NoEnumFileTable
      uploadApi={({ file }) => Api.postDataUpload({ files: file, contractId: id })}
      title={title}
      canEdit={canEdit}
      canEditItem={record => {
        return record.isEdit !== -1
      }}
      canDelete={record => {
        return record.isEdit !== -1
      }}
      columns={columns}
      params={params}
      functionCodeList={{
        fileList: 'contractFileListNew',
        download: 'newContractFileDownload',
        batchDownload: 'contractFileBatchDownload',
      }}
    />
  )
}
export default observer(Report)
