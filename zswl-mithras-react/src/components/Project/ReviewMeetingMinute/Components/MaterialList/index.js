import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components/Table'
import { getUserInfo } from '@/utils'

function ProjectReviewMeetingMinuteMaterialList({
  id,
  canEdit = true,
  taskActivityId,
  businessVersion,
  bizType = 'PROJ_REVIEW_MEET_MINUTE',
}) {
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'filename',
    },
    {
      title: '上传人',
      dataIndex: 'createByName',
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
    },
  ]
  const params = {
    mainId: id,
    moduleType: bizType,
    materialsType: bizType,
  }
  return (
    <>
    {/* taskActivityId=== 'userTask_jurySecretaryCollect' */}
    <p>资料文件</p>
    <NoEnumFileTable
      canEdit={ taskActivityId&&
        (
         taskActivityId=== 'userTask_jurySecretaryCollect'
        )}
      params={params}
      columns={columns}
      needApproval={false}
      // canUpload={true}
      canEditItem={false}
      canDelete={(record) => {
        return (record.createBy?.value || record.createBy) == getUserInfo().id
      }}
    />
    </>
  )
}

export default observer(ProjectReviewMeetingMinuteMaterialList)
