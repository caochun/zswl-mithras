import styles from './index.less'
import { observer } from '@zswl/admin'
import Api from '@/api/project/component/ReviewDetail/Report/api'
import { FileTable } from '@/components/Table'
import { Button } from '@zswl/components'
import { message } from 'antd'
import { InputColumn } from '@/components/Format'

const MODULE_TYPE = 'PROJ_REVIEW'
const Report = ({
  id: mainId,
  canEdit = true,
  businessVersion,
  processModel,
  processInstanceId,
  title,
}) => {
  const generate = async () => {
    const data = await Api.postReportGenerate({ projReviewId: mainId })
    message.success('报告生成成功', () => {
      window.open(`/preview/reportPreview/${data}`)
    })
  }

  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    InputColumn({ title: '上传人', dataIndex: 'createByName' }),
    InputColumn({
      title: '上传地点',
      dataIndex: 'location',
      width: 400,
    }),
    InputColumn({ title: '上传时间', dataIndex: 'createTime' }),
  ]

  const currentList = (list) =>
    list.filter((item) => {
      if (!processModel) {
        return (
          ['DUE_DILIGENCE_REPORT', 'BUSINESS_PRICING_APPROVAL_FORM', 'OTHER'].indexOf(item.value) >
          -1
        )
      }
      if (processModel === 'ProjReviewPricingApprovalFlow') {
        return ['BUSINESS_PRICING_APPROVAL_FORM'].indexOf(item.value) > -1
      }
      return ['DUE_DILIGENCE_REPORT', 'OTHER'].indexOf(item.value) > -1
    })

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    businessVersion,
    ext: {
      processInstanceId,
    },
  }
  return (
    <FileTable
      canBatchDownload
      enumType={'projReviewMaterialsEnum'}
      handleEnumType={currentList}
      uploadApi={({ file, fileType: materialsType }) =>
        Api.postReportUpload({
          file,
          projReviewId: mainId,
          materialsType,
        })
      }
      removeApi={({ id }, { businessType, belongId }) => {
        return Api.postReviewFileBatchRemove({
          ...params,
          fileIds: [id],
          mainId: businessType === 'VISIT_RECORD' ? belongId : mainId,
        })
      }}
      extra={[
        <Button type="primary" disabled={!canEdit} key="generate" onClick={generate}>
          生成报告
        </Button>,
      ]}
      params={params}
      title={<h3 className={styles.title}>{title}</h3>}
      canEdit={canEdit}
      columns={columns}
    />
  )
}
export default observer(Report)
