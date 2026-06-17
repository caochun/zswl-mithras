import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/pages/risk/publicMonitor/Column'
import { NoEnumFileTable } from '@/components'
import { getDescColumns } from '@/utils'
import Api from '@/api/risk/publicMonitor'

const nameColumns = [
  'ID',
  '标题',
  '客户名称',
  '舆情时间',
  '统一社会信用代码',
  '状态',
  '预警星级',
  '预警信号',
  '主体机构代码',
  '信息发布日期',
  '链接',
  '是否处置',
  '处置意见',
]

const Index = (props) => {
  const { canEditFlag, id, businessVersion, taskActivityId, processInstanceId } = props

  const canEditFlags = canEditFlag === 'true'
  const approvalCanEdit = ['riskControlManager', 'assetManagement'].includes(taskActivityId)
  const columns = getDescColumns(
    ALL_COLUMNS({
      canEdit: false,
      approvalCanEdit,
    }),
    nameColumns
  )
  const [detail, setDetail] = useState({})

  const getDetail = async () => {
    const res = await Api.postMonitorDetail({
      businessVersion,
      id,
    })
    setDetail(res ?? {})
  }

  const saveData = async (values) => {
    await Api.postMonitorFlowHandle({
      id,
      handleResult: values.handleResult,
      advisement: values.advisement,
      processInstanceId,
    })
    getDetail()
  }

  useEffect(() => {
    id && getDetail()
  }, [id])

  return (
    <div>
      <EditDescription
        detail={detail}
        saveData={saveData}
        canEdit={canEditFlags || approvalCanEdit}
        isLog={true}
        columns={columns}
      />
      <div style={{ marginTop: 20 }}></div>
      <NoEnumFileTable
        title={'附件'}
        canEdit={canEditFlags || approvalCanEdit}
        params={{
          moduleType: 'RISK_OPINION',
          mainId: id,
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </div>
  )
}
export default observer(Index)
