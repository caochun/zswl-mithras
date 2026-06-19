import { FileTable } from '@/components/Table'
import documentManagementLedgerApi from '@/api/archives/documentManagementLedger'

const Index = (props) => {
  const { id: mainId, canEdit, enumType = [] } = props

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: 'FUND_FILING',
    enumType,
  }

  const downloadApi = async ({ id }) => {
    await documentManagementLedgerApi.detail.download({ fileId: id })
  }

  const batchDownloadApi = async ({ fileIds }) => {
    await documentManagementLedgerApi.detail.batchDownload({
      id: mainId,
      moduleCode: 'FUND_FILING',
      fileIds,
    })
  }

  if (enumType.length === 0) {
    return null
  }

  return (
    <FileTable
      enumType={enumType}
      batchDownloadApi={batchDownloadApi}
      downloadApi={downloadApi}
      title={'归档资料'}
      canEdit={canEdit}
      canDownloadAll={true}
      hideRowEdit={true}
      canDelete={canEdit}
      canUpload={canEdit}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default Index
