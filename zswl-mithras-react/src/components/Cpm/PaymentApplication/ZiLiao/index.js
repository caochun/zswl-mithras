import { observer } from '@zswl/admin'
import Api from '@/api/cpm/payment/paymentApplicationMaterialsApi'
import { FileTable } from '@/components/Table'

const MODULE_TYPE = 'PAYMENT'

const Index = ({ mainId, businessVersion, title }) => {
  const columns = [{ title: '项目评审资料', dataIndex: 'name' }]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    businessVersion,
  }
  return (
    <>
      <FileTable
        title={title}
        tableApi={async () => {
          const result = await Api.postDataList({
            paymentId: mainId,
            businessVersion,
          })
          const newResult = []
          result.map((item) => {
            newResult.push({
              key: item.groupType,
              groupTypeName: item.fileDataList?.[0]?.materialTypeName,
              value: item.fileDataList?.map((i) => {
                return {
                  id: i.fileId,
                  filename: i.fileName,
                  key: i.materialType,
                  idType: i.idType,
                }
              }),
            })
          })
          return newResult
        }}
        canBatchDownload
        params={params}
        canEdit={false}
        columns={columns}
      />
    </>
  )
}
export default observer(Index)
