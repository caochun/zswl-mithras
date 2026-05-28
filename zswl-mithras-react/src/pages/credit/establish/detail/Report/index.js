import { App, Form, Modal, Table } from '@zswl/components'
import styles from '../index.less'
import { history, observer } from '@zswl/admin'
import Api from '@/api/groupCredit/projectApprovalReport'
import { FileTable } from '@/components'
import { downFile } from '@/utils'
import { useMemo } from 'react'

const Report = ({ id, canEdit = true, isProjSponsor, businessVersion }) => {
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
  const download = async (params, { fileName }) => {
    const res = await Api.postReportDownload(params, fileName)
    downFile(res)
  }
  const params = {
    mainId: id,
    moduleType: 'GROUP_CREDIT_ESTABLISH',
    businessVersion,
  }

  return (
    <FileTable
      enumType="groupCreditEstablishMaterialsEnum"
      title={<div className={styles.title}>立项报告</div>}
      canEdit={canEdit}
      columns={columns}
      uploadApi={({ file, fileType: materialsType }, config) => {
        return Api.postReportUpload({ file, groupCreditEstablishId: id, materialsType }, config)
      }}
      params={params}
    />
  )
}
export default observer(Report)
