import { observer } from '@zswl/admin'
import { FileTable, NoEnumFileTable } from '@/components'

const MODULE_TYPE = 'PAYMENT'

const Index = ({ mainId, businessVersion, canEdit }) => {
  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    businessVersion,
    ext: { queryType: 'LOAN_APPROVAL' },
  }
  return (
    <div style={{ marginTop: 12 }}>
      <FileTable
        title={'资料清单'}
        // canEdit={!canEdit}
        canEdit={true}
        enumType={'lendingMaterialType'}
        handleEnumType={(type) => type.filter((item) => ['LOAN_APPROVAL'].includes(item.value))}
        functionCodeList={{
          upload: 'paymentReviewInAdvanceFileUpload',
          delete: 'paymentReviewInAdvanceFileDelete',
          fileList: 'paymentReviewInAdvanceFileListGroup',
        }}
        params={params}
        columns={[
          { title: '资料名称', dataIndex: 'name' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </div>
  )
}
export default observer(Index)
