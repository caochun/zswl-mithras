import { NoEnumFileTable } from '@/components/Table'
import { observer } from '@zswl/admin'

const MODULE_TYPE = 'CREDIT_REPORT_SELECT'
const CreditReportSearchReportFiles = ({ mainId, canEdit = true, dataSource = [] }) => {
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  return (
    <div>
      <h3>客户征信报告</h3>
      {dataSource.map((item) => (
        <div key={item.id}>
          <NoEnumFileTable
            title={<div style={{ fontSize: 12 }}>客户名称:{item.clientName}</div>}
            canEdit={canEdit}
            columns={columns}
            canBatchDownload
            params={{
              mainId: item.id,
              moduleType: MODULE_TYPE,
              materialsTypes: ['CLIENT_CREDIT_REPORT'],
              ext: {
                clientId: item.clientId,
              },
            }}
          />
        </div>
      ))}
    </div>
  )
}

export default observer(CreditReportSearchReportFiles)
