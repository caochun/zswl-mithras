import { observer } from '@zswl/admin'
import Api from '@/api/contract/component/ChangeMaterials/api'
import { FileTable } from '@/components/Table'
import { getUserInfo } from '@/utils'

const Report = ({ id, canEdit = true, businessVersion, taskActivityId, taskStatus }) => {
  const isYunYingNode = ['userTask_yunYingGuanLiReview', 'userTask_yunYingGuanLi'].includes(
    taskActivityId
  )
  const canEditStatus = taskStatus === '1'
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
    },
  ]

  const params = {
    mainId: id,
    moduleType: 'CONTRACT',
    ext: {
      queryType: 'CHANGE',
    },
    businessVersion,
  }
  return (
    <FileTable
      enumType={'contractChangeMaterialEnum'}
      uploadApi={({ file, fileType: contractType }) =>
        Api.postDataUpload({ file, contractType, contractId: id })
      }
      title={<div className={'z-sub-title'}>变更材料</div>}
      canEdit={isYunYingNode && canEditStatus ? true : canEdit}
      canDelete={(record) => {
        if (isYunYingNode) {
          //  合同变更流程中，变更材料模块放开运营上传、编辑、删除的权限（运营经办/复核可编辑和删除项目经理上传的文本，项目经理仅能删除和编辑自己上传的）
          return canEditStatus
        }
        // 发起人节点
        if (taskActivityId === 'userTask_startUser') {
          return (
            canEditStatus &&
            (record.uploadByPostList?.value?.includes('projmanager') ||
              getUserInfo().id === (record.createBy?.value || record.createBy))
          )
        }
        return canEdit
      }}
      columns={columns}
      params={params}
      functionCodeList={{
        download: 'newContractFileDownload',
        batchDownload: 'contractFileBatchDownload',
      }}
    />
  )
}
export default observer(Report)
