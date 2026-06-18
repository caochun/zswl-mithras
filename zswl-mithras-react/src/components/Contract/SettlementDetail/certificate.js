import { observer } from '@zswl/admin'
import Api from './api'
import { NoEnumFileTable } from '@/components/Table'
import { isYunYingBuFuZeRen, isYunYingGuanLi, isContractSettlement, getUserInfo } from '@/utils/auth'

const Certificate = ({ id, canEdit = false, businessVersion, title, startUserId }) => {
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
    },
  ]

  const params = {
    mainId: id,
    moduleType: 'CONTRACT',
    materialsType: 'CONTRACT_SETTLE_OWN',
    MaterIalsTypes: ['CONTRACT_SETTLE_OWN'],
    ext: {
      queryType: 'SETTLE_OWN',
    },
    businessVersion,
  }
  const isShowEdit = isYunYingGuanLi() || isYunYingBuFuZeRen() || !!startUserId;
  return (
    <NoEnumFileTable
      uploadApi={({ file }) => Api.postDataUpload({ files: file, contractId: id })}
      title={title}
      canEdit={canEdit}
      canDelete={false}
      canUpload={false}
      canEditItem={isShowEdit}
      canBatchDownload={false}
      columns={columns}
      params={params}
      functionCodeList={{
        fileList: 'contractFileListNew',
        download: 'newContractFileDownload',
        batchDownload: 'contractFileBatchDownload',
      }}
    />
  )
}
export default observer(Certificate)
