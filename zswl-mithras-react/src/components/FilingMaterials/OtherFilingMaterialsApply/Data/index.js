import { FileTable } from '@/components'
import { Form, Input } from 'antd'
import { useEffect, useState } from 'react'
import Api from '../api'
const MODULE_TYPE = 'OTHER_FILING'

const Index = (props) => {
  const { id: mainId, canEdit, enumType = [], showDataList, store } = props

  const [materialsType, setMaterialsType] = useState('')

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
  }

  const getMaterialsDesc = async () => {
    const result = await store.getMaterialsDesc(mainId)
    setMaterialsType(result)
  }
  useEffect(() => {
    getMaterialsDesc()
  }, [mainId])

  const canEditItem = (record) => {
    if (!canEdit) return false
    return (record.materialsType?.value || record.materialsType) === 'BASIC_INFORMATION'
  }

  const handleSaveMaterialsDesc = async () => {
    try {
      await Api.saveMaterialsDesc({
        id: mainId,
        materialsDesc: materialsType,
      })
    } catch (error) {
      console.error('保存资料类型失败:', error)
    }
  }

  const batchDownloadApi = async ({ fileIds }) => {
    await Api.batchDownload({
      id: mainId,
      moduleCode: MODULE_TYPE,
      fileIds,
    })
  }

  if (enumType.length === 0) {
    return null
  }

  const renderActions = () => {
    if (!showDataList) {
      return null
    }
    return (
      <Form.Item label="资料类型" required style={{ marginBottom: 0 }}>
        <Input
          value={materialsType}
          disabled={!canEdit}
          onChange={(e) => setMaterialsType(e.target.value)}
          onBlur={handleSaveMaterialsDesc}
          placeholder="请输入资料类型"
          maxLength={100}
          style={{ width: 300 }}
        />
      </Form.Item>
    )
  }

  return (
    <FileTable
      enumType={enumType}
      params={params}
      removeApi={({ id }) => {
        return Api.remove({
          ...params,
          fileId: id,
        })
      }}
      batchDownloadApi={batchDownloadApi}
      canEditItem={canEditItem}
      uploadType="rowUpload"
      canDownloadAll={true}
      title={'归档资料'}
      canEdit={canEdit}
      columns={columns}
      actions={renderActions()}
    />
  )
}

export default Index
