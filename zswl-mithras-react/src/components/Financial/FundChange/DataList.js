import { FileTable } from '@/components/Table'
const Index = (props) => {
  const { financingId: mainId, canEdit = true, businessVersion } = props

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: 'FUND_FINANCING',
    businessVersion,
  }
  return (
    <FileTable
      enumType={'fundFinancingMaterialsEnumWorking'}
      title={'补充协议'}
      canEdit={canEdit}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default Index
