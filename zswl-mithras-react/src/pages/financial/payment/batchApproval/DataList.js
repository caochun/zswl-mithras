import { observer } from '@zswl/admin'
import Api from '@/api/common/fileList'
import { NoEnumFileTable } from '@/components'

const MODULE_TYPE = 'BATCH_FUND_RECEIPT_REPAY'
const Index = ({ id: mainId, businessVersion, canEdit = true }) => {
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    businessVersion,
    materialsType: 'DEFAULT',
  }
  return <NoEnumFileTable title={'资料清单'} params={params} canEdit={canEdit} columns={columns} />
}
export default observer(Index)
