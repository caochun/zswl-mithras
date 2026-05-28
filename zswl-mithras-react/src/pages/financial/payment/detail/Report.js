import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components'
import { hasPermission } from '@/utils'

const MODULE_TYPE = 'FUND_RECEIPT_REPAY'
const Report = ({ id: mainId, businessVersion, canEdit }) => {
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
  return (
    <NoEnumFileTable
      title={'付款资料'}
      params={params}
      // uploadApi={({ file }) => Api.postFileUpload({ ...params, file })}
      canEdit={canEdit && hasPermission('fundReceiptRepayFileUpload')}
      columns={columns}
    />
  )
}
export default observer(Report)
