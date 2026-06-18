import { FileTable } from '@/components/Table'
import { getQuery, observer } from '@zswl/admin'
import fileList from '@/api/common/fileList'

const Index = (props) => {
  const { id: mainId, canEdit = true, businessVersion } = props

  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const isFormApproval = getQuery('typeId') == 'approval' && getQuery('tab') !== 'revocation'
  const params = {
    mainId,
    moduleType: 'FUND_RECEIPT_REPAY',
    businessVersion,
  }
  return (
    <FileTable
      enumType={'fundFinancingMaterialsEnum'}
      title={'资料清单'}
      canEdit={false}
      columns={columns}
      tableApi={() =>
        fileList.postFundReceiptReapyList({
          id: mainId,
          version: isFormApproval ? businessVersion : undefined,
        })
      }
      canBatchDownload
      hasFormApproval={false}
      params={params}
    />
  )
}

export default observer(Index)
