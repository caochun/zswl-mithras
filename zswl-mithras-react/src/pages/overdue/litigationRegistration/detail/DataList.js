import { FileTable } from '@/components/Table'
import { useMemo } from 'react'

const Index = (props) => {
  const { mainId, canEdit = true, businessVersion } = props

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: 'LITIGATION_REGISTRATION',
    businessVersion,
  }

  return (
    <FileTable
      enumType={'litigationFileType'}
      title={'资料清单'}
      canEdit={canEdit}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default Index
