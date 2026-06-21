import { FileTable } from '@/components/Table'
import Api from '@/api/filingMaterials/afterFilingMaterialsApplyApi'

const FilingMaterialsAfterApplyData = (props) => {
  const { id: mainId, canEdit, enumType = [] } = props

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    {
      title: '上传地点',
      dataIndex: 'location',
    },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: 'AFTER_LEASING_FILING',
  }

  const batchDownloadApi = async ({ fileIds }) => {
    await Api.batchDownload({
      id: mainId,
      moduleCode: 'AFTER_LEASING_FILING',
      fileIds,
    })
  }

  const canEditItem = (record) => {
    if (!canEdit) {
      return false
    }
    return (record.materialsType?.value || record.materialsType) === 'BASIC_INFORMATION'
  }

  const disableFolderAction = (record) => {
    return record.enumType !== 'BASIC_INFORMATION'
  }

  if (enumType.length === 0) {
    return null
  }

  const enumList = enumType.filter((item) => item.value === 'BASIC_INFORMATION')
  return (
    <FileTable
      enumType={enumType}
      enumList={enumList}
      batchDownloadApi={batchDownloadApi}
      removeApi={({ id }) => {
        return Api.remove({
          ...params,
          fileId: id,
        })
      }}
      title={'归档资料'}
      uploadType="rowUpload"
      canDownloadAll={true}
      canEdit={canEdit}
      canDelete={canEditItem}
      disableFolderAction={disableFolderAction}
      canUpload={canEditItem}
      canEditItem={canEditItem}
      columns={columns}
      canBatchDownload
      params={params}
      foldKeys={(record) => record.enumType === 'BASIC_INFORMATION'}
    />
  )
}

export default FilingMaterialsAfterApplyData
