import { FileTable } from '@/components/Table'
import { useRef, forwardRef, useState } from 'react'
import { observer } from '@zswl/admin'
import { Modal, Button } from '@zswl/components'
import { Switch, Space } from 'antd'
import Api from '@/api/lease/maintainApi'
import { downUrl, getUserInfo } from '@/utils'

const modalTitleStyle = {
  color: 'red',
  fontWeight: 'bold',
  cursor: 'pointer',
}

const LeaseMaintainDataFileList = (props) => {
  const { id: mainId, canEdit, businessVersion, taskActivityId, taskStatus, startUserId,modelKey } = props
  const isProjmanagerInProcess = taskActivityId === 'projManager'
  // 运营经理
  const isOperationmanagementagentInProcess = taskActivityId === 'operationManagement'
  // 运营复核
  const isOperationManagementReviewInProcess =
    taskActivityId === 'userTask_operationManagementReview'
  const isLegalManagerUserInProcess = taskActivityId === 'legalManagerUser'

  // 是否添加水印 0 不添加， 1添加
  const [needWatermark, setWatermark] = useState(1)
  const ref = useRef()

  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const onWaterMarkChange = (value) => {
    setWatermark(value ? 1 : 0)
  }

  const params = {
    mainId,
    moduleType: 'LEASE_DATA_LIST',
    businessVersion,
  }

  const modal = Modal.useStore({
    onOpen: async () => {
      return {}
    },
    onFinish: async () => {
      modal.close()
    },
  })

  const downloadTemplate = async () => {
    const result = await Api.postLeaseFileTemplateList({
      templateType: '租赁物模版-中登网查重',
      fileName: '租赁物审核模板-中登网查重.xlsx',
    })

    const url = downUrl('/file/batch/download', {
      mainId: result.list[0]?.id,
      fileId: result.list[0]?.fileId,
      moduleType: 'FILE_TEMPLATE',
    })
    window.open(url)
  }

  const canEditByRecord = (record) => {
    if (isOperationManagementReviewInProcess) {
      // 运营复核可以 删除自己的 和运营经办的文件
      return (
        taskStatus === '1' &&
        (record.uploadByPostList?.value?.includes('operationManagement') ||
          record?.createBy?.value == getUserInfo().id)
      )
    }
    return taskStatus === '1' && record?.createBy?.value == getUserInfo().id
  }

  const canDeleteByRecord = (record) => {
    if (isOperationManagementReviewInProcess) {
      // 运营复核可以 删除自己的 和运营经办的文件
      return (
        taskStatus === '1' &&
        (record.uploadByPostList?.value?.includes('operationManagement') ||
          record?.createBy?.value == getUserInfo().id)
      )
    }
    // 运营经理 operationManagement
    // 运营角色经办 taskStatus === '1'
    if (['LeaseCreateFlow', 'LeaseModifyFlow'].includes(modelKey) && 
        isOperationmanagementagentInProcess && 
        taskStatus === '1') {
        return true
    }
    return taskStatus === '1' && record?.createBy?.value == getUserInfo().id
  }

  return (
    <div>
      <FileTable
        showChangeType={false}
        uploadApi={async (data) => {
          // 因为水印加参数了,所以重写
          return await Api.postLeaseDataListFileUpload({
            ...data,
            ...params,
            needWatermark:
              isOperationmanagementagentInProcess || isOperationManagementReviewInProcess
                ? needWatermark
                : undefined,
          })
        }}
        // 流程节点在上传人时，上传人可删除自己上传的文档
        canDelete={canDeleteByRecord}
        functionCodeList={{
          remove:'maintainFileBatchRemove'
        }}
        canEditItem={canEditByRecord}
        enumType={'leaseFileTypeEnums'}
        handleEnumType={(type) => {
          // 运营经理、项目经理 上传的枚举限制
          if (isProjmanagerInProcess) {
            return type.filter((item) => item.label?.startsWith('项目经理'))
          } else if (isOperationmanagementagentInProcess || isOperationManagementReviewInProcess) {
            return type.filter(
              (item) =>
                item.label?.startsWith('运营') ||
                item.value?.includes('OPERATION_MANAGER_REVIEW_SUBMISSION')
            )
          } else if (isLegalManagerUserInProcess) {
            return type.filter((item) =>
              item.value?.includes('OPERATION_MANAGER_REVIEW_SUBMISSION')
            )
          }
          return type
        }}
        title={'资料清单'}
        uploadTips={
          <Space direction="vertical">
            <div style={modalTitleStyle}>
              <span onClick={modal.open}>点击查看【查重文件】资料要求</span>
            </div>
            {/* 仅在运营经理模块，增加【是否添加水印】的选项 */}
            {(isOperationmanagementagentInProcess || isOperationManagementReviewInProcess) && (
              <div>
                <span>上传是否添加水印：</span>
                <Switch defaultChecked={!!needWatermark} onChange={onWaterMarkChange} />
              </div>
            )}
          </Space>
        }
        //  运营经理、项目经理有编辑权限，上传取对应岗位的枚举
        canEdit={
          taskStatus === '1' &&
          canEdit &&
          (isProjmanagerInProcess ||
            isOperationmanagementagentInProcess ||
            isOperationManagementReviewInProcess ||
            isLegalManagerUserInProcess)
        }
        columns={columns}
        canBatchDownload
        params={params}
        ref={ref}
        actions={[]}
      />
      <Modal
        store={modal}
        title="上传提示"
        getContainer={() => document.body}
        footer={
          <Button type="primary" onClick={modal.close}>
            我知道了
          </Button>
        }
      >
        <Space direction="vertical">
          <div>
            <span style={modalTitleStyle}>1、查询范围：</span>
            需同时查询中登网登记信息（交易业务类型包含①融资租赁②所有权保留登记、③生产设备、原材料、半成品、产品抵押、④其他可以登记的动产和权利担保）、公示系统抵押信息（发票开具日期/企业注册日期在2021年1月1日之后的无需公示系统查询）。
          </div>
          <div>
            <span style={modalTitleStyle}>2、查询主体：</span>
            承租人（如权属文件涉及曾用名、分公司、联合承租人的，则应包含相应主体）、划拨函划出方。
          </div>
          <div>
            <span style={modalTitleStyle}> 3、附件要求：</span>
            需下载所有相关中登网及公示系统附件上传，查询截图模版如
            <a onClick={downloadTemplate}>附件</a>。
          </div>
        </Space>
      </Modal>
    </div>
  )
}

export default observer(forwardRef(LeaseMaintainDataFileList))
