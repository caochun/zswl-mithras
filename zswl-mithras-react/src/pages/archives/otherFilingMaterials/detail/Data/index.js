import { FileTable } from '@/components/Table'
import { Form, Input } from 'antd'
import otherFilingMaterialsApi from '@/api/archives/otherFilingMaterials'

const MODULE_TYPE = 'OTHER_FILING'

const Index = (props) => {
  const { id: mainId, canEdit, enumType = [], materialsDesc } = props
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
  }

  const renderActions = () => {
    if (materialsDesc === 'null' || !materialsDesc) {
      return null
    }
    return (
      <Form.Item label="资料类型" required style={{ marginBottom: 0 }}>
        <Input value={materialsDesc} disabled={true} maxLength={100} style={{ width: 300 }} />
      </Form.Item>
    )
  }

  const batchDownloadApi = async ({ fileIds }) => {
    await otherFilingMaterialsApi.batchDownload({
      id: mainId,
      moduleCode: MODULE_TYPE,
      fileIds,
    })
  }

  const canEditItem = (record) => {
    if (!canEdit) return false
    return (record.materialsType?.value || record.materialsType) === 'BASIC_INFORMATION'
  }

  if (enumType.length === 0) {
    return null
  }

  return (
    <FileTable
      enumType={enumType}
      params={params}
      removeApi={({ id }) => {
        return otherFilingMaterialsApi.remove({
          ...params,
          fileId: id,
        })
      }}
      canEditItem={canEditItem}
      batchDownloadApi={batchDownloadApi}
      canDownloadAll={true}
      canUpload={canEdit}
      canDelete={canEdit}
      title={'归档资料'}
      canEdit={canEdit}
      columns={columns}
      uploadType="rowUpload"
      actions={renderActions()}
    />
  )
}

export default Index
