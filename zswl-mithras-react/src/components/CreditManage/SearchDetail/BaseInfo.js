import { EditDescription } from '@/components/Table'
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import creditReportApi from '@/api/credit/creditReportApi'

const nameColumns = [
  '客户信息',
  '授权起始日',
  '查询版本',
  '信用报告封装格式',
  '关联项目编号',
  '关联项目名称',
]

function CreditReportSearchBaseInfo({ canEdit = true, store }) {
  const dataSource = store.page.getData()

  const columns = getDescColumns(ALL_COLUMNS({ creditReportId: dataSource?.id }), nameColumns)
  const saveData = async (values) => {
    const clientInfos = (values?.clientInfos || []).map((v) => ({
      reportId: dataSource?.id,
      clientId: v?.clientId,
      clientName: v?.clientName,
      cscCode: v?.cscCode,
      zhongZhengCode: v?.zhongZhengCode,
      selectGoal: v?.selectGoal,
      id: v?.id,
    }))

    await creditReportApi.postBaseSave({
      id: dataSource?.id,
      clientInfos,
      authorizationBeganDate:
        values?.authorizationBeganDate &&
        moment(values?.authorizationBeganDate).format('YYYY-MM-DD'),
      projName: values?.projName,
      selectVersion: values?.selectVersion,
      reportFormat: values?.reportFormat,
    })
    store.page.init()
  }

  return (
    <EditDescription
      title="本次查询信息"
      detail={dataSource}
      canEdit={canEdit}
      initEdit={false}
      columns={columns}
      style={{ marginBottom: 12 }}
      saveData={saveData}
    />
  )
}

export default observer(CreditReportSearchBaseInfo)
