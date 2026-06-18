import { FileTable } from '@/components/Table'
import { useRef, forwardRef, useImperativeHandle } from 'react'
import { observer } from '@zswl/admin'
import fileListApi from '@/api/common/fileList'
import { getUserInfo } from '@/utils'

const Index = (props) => {
  // 租赁物清单: 流程中项目经理、运营和法务可删除和上传
  // TODO: 租赁物确认函: 带入合同流程的不允许项目经理删除或上传，运营和法务节点可以
  const { id: mainId, canEdit, businessVersion, fileListRef, taskActivityId, taskStatus,modelKey } = props
  const isOperationmanagementagentInProcess = taskActivityId === 'operationManagement'
  console.log('modelKey',modelKey,'taskStatus',taskStatus,'isOperationmanagementagentInProcess',isOperationmanagementagentInProcess)
  const ref = useRef()

  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: 'LEASE_TEXT',
    businessVersion,
  }

  useImperativeHandle(fileListRef, () => ({
    search: () => {
      ref.current?.table?.search()
    },
  }))

  return (
    <div ref={fileListRef}>
      <FileTable
        showChangeType={false}
        uploadApi={async (data) => {
          // 因为水印加参数了,所以重写
          return await fileListApi.postFileUpload(
            { ...data, ...params, needWatermark: 1 },
            'leaseDataListFileUpload'
          )
        }}
        enumType={'leaseTextFileEnum'}
        title={'租赁物文本'}
        canEdit={canEdit}
        columns={columns}
        canBatchDownload
        params={params}
        functionCodeList={{
          remove:'maintainFileBatchRemove'
        }}
        ref={ref}
        // 流程节点在上传人时，上传人可删除自己上传的文档。后端也有判断
        canDelete={(record) => {
          return (taskStatus === '1' && record?.createBy?.value == getUserInfo().id) || (['LeaseCreateFlow', 'LeaseModifyFlow'].includes(modelKey) && 
          isOperationmanagementagentInProcess && 
          taskStatus === '1')
        }}
        canEditItem={(record) => {
          return taskStatus === '1' && record?.createBy?.value == getUserInfo().id
        }}
        actions={[]}
      />
    </div>
  )
}

export default observer(forwardRef(Index))
