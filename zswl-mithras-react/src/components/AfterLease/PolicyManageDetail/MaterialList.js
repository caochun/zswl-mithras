import { observer } from '@zswl/admin'
import { FileTable } from '@/components/Table'

const Index = ({ id, detail }) => {
  const columns = [
    { title: '保险单号', dataIndex: 'name', rename: '资料' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId: id,
    moduleType: 'POLICY',
    ext: {
      contractId: detail.contractId,
    },
  }
  return (
    <FileTable
      enumType={'policyMaterialsEnum'}
      title={'资料清单'}
      canEdit={false}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default observer(Index)
