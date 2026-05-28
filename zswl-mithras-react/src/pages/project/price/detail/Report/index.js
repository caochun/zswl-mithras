import styles from '../index.less'
import { observer } from '@zswl/admin'
import Api from './api'
import { FileTable } from '@/components'
import { Button } from '@zswl/components'
import { message } from 'antd'

const MODULE_TYPE = 'PROJ_PRICING'
const Report = ({
  id: mainId,
  canEdit = true,
  businessVersion,
  processModel,
  processInstanceId,
  title,
}) => {
  const generate = async () => {
    const data = await Api.postReportGenerate({ projPricingId: mainId })
    message.success('报告生成成功', () => {
      window.open(`/preview/reportPreview/${data}`)
    })
  }

  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const currentList = (list) => {
    return list.filter((item) => {
      if (!processModel) {
        return ['BUSINESS_PRICING_APPROVAL_FORM', 'PRICING_OTHER'].indexOf(item.value) > -1
      }
      if (processModel === 'ProjPricingApprovalFlow') {
        return [].indexOf(item.value) > -1
      }
    })
  }

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
      enumType={'projPricingMaterialsEnum'}
      handleEnumType={currentList}
      uploadApi={({ file, fileType: materialsType }) =>
        Api.postReportUpload({
          file,
          projPricingId: mainId,
          materialsType,
        })
      }
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
