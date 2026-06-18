import styles from './index.less'
import { observer, getQuery } from '@zswl/admin'
import Api from './api'
import { FileTable } from '@/components/Table'
import { App, Button, Form, Modal, ModalStore } from '@zswl/components'
import { Checkbox, Tooltip, message } from 'antd'
import { getUserInfo } from '@/utils'
import { useMemo, useRef } from 'react'

const MODULE_TYPE = 'CONTRACT'
const Report = ({
  id: mainId,
  canEditFlag = true,
  yuYingJingBanCanEdit,
  businessVersion,
  taskActivityId,
  modelKey,
  isFormChangeType,
  title,
  taskStatus,
  baseStore = {},
}) => {
  const canEdit = canEditFlag || yuYingJingBanCanEdit
  const fileTableRef = useRef(null)
  baseStore.fileTableRef = fileTableRef

  const isFormApproval = getQuery('typeId') == 'approval'
  const curTab = getQuery('curTab')
  const tab = getQuery('tab')

  console.log({ taskActivityId })

  const modal = useMemo(
    () =>
      new ModalStore({
        onFinish: async (values) => {
          await generateFile(values)
          modal.close()
        },
      }),
    []
  )
  const handleGenerate = () => {
    // if (isFormApproval) {
    //   modal.open()
    // } else {
    return generateFile()
    // }
  }
  const generateFile = async (values = {}) => {
    await Api.postGenerateFile({ contractId: mainId, ...values })
    fileTableRef.current.table?.search()
    message.info('生成合同成功')
  }
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
      render: (text, record) => {
        const systemGenerate = record?.systemGenerate?.value ?? record?.systemGenerate
        const name = record.name?.value ?? record?.name
        const isRed = record.name?.isChange
        const isEdit = record.isEdit?.value ?? record.isEdit

        const title = (
          <div>
            <span style={{ color: isRed && 'red' }}>{name}</span>
            {systemGenerate && isEdit === 0 ? <span className={'z-tag'}>自动生成</span> : ''}
          </div>
        )
        const render = fileTableRef.current.filedRender
        return render(title, record, {
          dataIndex: 'name',
        })
      },
      width: 500,
    },
    {
      title: '上传人',
      dataIndex: 'createByName',
      width: 180,
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
      width: 180,
    },
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    ext: {
      queryType: 'COMMON',
    },
    businessVersion,
  }
  const checkOptions = App.getData().optionsType.contractTypeEnum

  const isYunYingNode = ['userTask_yunYingGuanLiReview', 'userTask_yunYingGuanLi'].includes(
    taskActivityId
  )
  const canEditStatus = taskStatus === '1'

  return (
    <>
      <h3 className={styles.title}>{title}</h3>
      <FileTable
        afterDelete={() => {
          baseStore.chengZhuRenStore?.$table?.search()
          baseStore.danBaoStore?.$table?.search()
          baseStore.diYaStore?.$table?.search()
          baseStore.zhiYaStore?.$table?.search()
        }}
        showChangeType={isFormChangeType}
        ref={fileTableRef}
        enumType={'contractTypeEnum'}
        uploadApi={({ file, fileType: materialsType }) =>
          Api.postDataUpload({
            file,
            contractId: mainId,
            contractType: materialsType,
          })
        }
        canBatchDownload
        canEditItem={() => true}
        extra={[
          <Button type="primary" disabled={!canEditFlag} key="generate" onClick={handleGenerate}>
            生成合同
          </Button>,
        ]}
        params={params}
        title={<div className={'z-sub-title'}>未签约版</div>}
        // 变更材料模块放开运营上传、编辑、删除的权限（运营经办/复核可编辑和删除项目经理上传的文本，项目经理仅能删除和编辑自己上传的）
        canEdit={
          isFormApproval
            ? !!((isYunYingNode || taskActivityId === 'userTask_startUser') && canEditStatus)
            : canEdit
        }
        canDelete={(record) => {
          // 审批流->待审批、法务有权限删除系统自动生成的文档
          const isSystemGenerate = record.systemGenerate?.value ?? record.systemGenerate
          const isEdit = record.isEdit?.value ?? record.isEdit

          const baeCase = canEditStatus && record.name?.changeType !== 'REMOVE'

          const newIsSystemGenerate = isSystemGenerate == 1 && isEdit === 0

          // 流程中的审批节点
          if (isFormApproval) {
            if (taskActivityId === 'userTask_lawManager') {
              return baeCase && newIsSystemGenerate
            }
            // 对修改过的合同文本，仍支持运营删除，仅运营经办/复核可删除，其他有删除权限的节点（项目经理等）仅能在自己审批节点删除自己上传/系统自动生成的文件。 运营删除，仅运营经办/复核可删除，删除权限包括可删除项目经理上传的文件
            if (isYunYingNode) {
              return baeCase
            }
            // 发起人节点,能删除 自己上传的 和 待自动生成标的
            if (taskActivityId === 'userTask_startUser') {
              const isUploadBySelf =
                getUserInfo().id === (record.createBy?.value ?? record.createBy)
              return baeCase && (newIsSystemGenerate || isUploadBySelf)
            }
          }
          return canEdit
        }}
        columns={columns}
        functionCodeList={{
          download: 'newContractFileDownload',
          batchDownload: 'contractFileBatchDownload',
        }}
      />
      <Modal title="生成合同" store={modal} destroyOnClose>
        <Form>
          <Form.Item label="重新生成合同类型" name="generateContractType" valuePropName="checked">
            <Checkbox.Group options={checkOptions}></Checkbox.Group>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
export default observer(Report)
