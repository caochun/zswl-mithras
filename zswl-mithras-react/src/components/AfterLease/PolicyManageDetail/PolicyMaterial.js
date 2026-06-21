import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components/Table'

const AfterLeasePolicyManageDetailPolicyMaterial = ({ id, detail }) => {
  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId: id,
    moduleType: 'POLICY',
  }
  return (
    <NoEnumFileTable
      tableApi={() => {
        return {
          list: detail.materials ?? [],
        }
      }}
      title={'保单资料'}
      canEdit={false}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default observer(AfterLeasePolicyManageDetailPolicyMaterial)
