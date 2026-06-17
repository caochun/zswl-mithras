import { FileTable } from '@/components'
import Api from '../api'
const Index = (props) => {
  const { id: mainId, disabled, businessVersion } = props

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: 'FUND_DIRECT_FINANCING',
    businessVersion,
  }
  const downloadApi = async ({ id }) => {
    return Api.download({ fileId: id })
  }
  const batchDownloadApi = async ({ fileIds }) => {
    return Api.batchDownload({
      id: mainId,
      moduleCode: 'FUND_DIRECT_FINANCING',
      fileIds,
    })
  }
  return (
    <FileTable
      enumType={'fundDirectFinancingMaterialsEnum'}
      title={'资料清单'}
      canEdit={!disabled}
      downloadApi={downloadApi}
      batchDownloadApi={batchDownloadApi}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default Index
