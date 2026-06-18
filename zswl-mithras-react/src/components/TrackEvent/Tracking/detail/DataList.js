import { NoEnumFileTable } from '@/components/Table'

const Index = (props) => {
  const { mainId, canEdit = true, businessVersion } = props

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: 'TRACK_EVENT',
    businessVersion,
  }
  return (
    <NoEnumFileTable
      title={'资料清单'}
      canEdit={canEdit}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default Index
