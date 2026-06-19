import styles from '../index.less'
import { observer } from '@zswl/admin'
import Api from '@/api/credit/groupCreditReviewReportApi'
import { FileTable } from '@/components/Table'

const Report = ({ id, canEdit = true, businessVersion }) => {
  const columns = [
    {
      title: '资料类别',
      dataIndex: 'name',
    },
    // {
    //   title: '报告名称',
    //   dataIndex: 'fileName',
    // },
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
    moduleType: 'GROUP_CREDIT_REVIEW',
    businessVersion,
  }
  return (
    <FileTable
      enumType={'projReviewMaterialsEnum'}
      uploadApi={({ file, fileType: materialsType }) =>
        Api.postReportUpload({ file, groupCreditReviewId: id, materialsType })
      }
      handleEnumType={(type) => {
        return type.filter((item) => {
          if (['DUE_DILIGENCE_REPORT', 'OTHER'].indexOf(item.value) > -1) {
            return item
          }
        })
      }}
      params={params}
      title={<div className={styles.title}>授信评审资料</div>}
      canEdit={canEdit}
      columns={columns}
    />
  )
}
export default observer(Report)
