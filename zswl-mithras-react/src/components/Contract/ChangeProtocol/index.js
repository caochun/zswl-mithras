import { observer } from '@zswl/admin'
import Api from '@/api/contract/component/ChangeProtocol/api'
import { NoEnumFileTable } from '@/components/Table'
import { downFile } from '@/utils/downFunction'

const Report = ({ id, canEdit = true, changeType, businessVersion, title }) => {
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'fileName',
    },
  ]

  const params = {
    mainId: id,
    moduleType: 'CONTRACT',
    businessVersion,
  }

  const tableApi = async () => {
    const res = await Api.postDataList({ contractId: id, changeType, businessVersion })
    return {
      list: res.map((item) => {
        return {
          ...item,
          id: item.fileId,
          name: item.fileName,
        }
      }),
    }
  }
  const downloadApi = async ({ id }) => {
    const res = await Api.postExtraMaterialsDownload({ ids: [id] })
    downFile(res)
  }
  const batchDownloadApi = async (ids) => {
    const res = await Api.postExtraMaterialsDownload({ ids })
    downFile(res)
  }

  return (
    <NoEnumFileTable
      tableApi={tableApi}
      downloadApi={downloadApi}
      batchDownloadApi={batchDownloadApi}
      uploadApi={({ file }) => Api.postDataUpload({ fileArray: file, contractId: id, changeType })}
      title={title}
      canEdit={canEdit}
      columns={columns}
      params={params}
      functionCodeList={{
        download: 'contractsettleextramaterialsdownload',
        batchDownload: 'contractsettleextramaterialsdownload',
      }}
      canEditItem={(record) => record.isEdit !== -1}
      canDelete={(record) => record.isEdit !== -1}
    />
  )
}
export default observer(Report)
