import { EditDescription } from '@/components/Table'
import { getDescColumns, getKeyOptionsLabelMapPlus } from '@/utils'
import ALL_COLUMNS from './Column'
import { observer } from '@zswl/admin'
import moment from 'moment'

const AfterLeaseExternalCheckQueryReportLessee = ({
  canEditFlag = true,
  detail,
  anchorId,
  title,
  store,
}) => {
  const { id: autoClientId } = detail

  const columnName = [
    {
      title: '客户名称',
      rename: getKeyOptionsLabelMapPlus('clientRole')[detail.clientRole],
    },
    '查询区间',
    '1.全国企业信用信息公示系统查询',
    '2.全国法院被执行人或被纳入失信人信息查询',
    '3.裁判文书网',
    '4.中登网登记及抵押登记',
    '5.信用报告(每半年查询一次)',
    '6.其它(如有)',
  ]
  const columns = getDescColumns(ALL_COLUMNS, columnName)
  const newDetail = {
    ...detail,
    queryTime: detail.queryTimeFrom
      ? [moment(detail.queryTimeFrom), moment(detail.queryTimeTo)]
      : undefined,
  }
  return (
    <div id={anchorId}>
      <EditDescription
        title={title}
        style={{ marginTop: 20, marginBottom: 20 }}
        detail={newDetail}
        saveData={(values) => store.onSaveChengZuRen(values, autoClientId)}
        canEdit={canEditFlag}
        columns={columns}
        labelStyle={{ background: '#F5F6FA', width: 320 }}
      />
    </div>
  )
}

export default observer(AfterLeaseExternalCheckQueryReportLessee)
