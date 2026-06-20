import { Table, Page } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import store from './store'
import newFtpBaseInfoApi from '@/api/budget/pricing/ftp/newFtpBaseInfoApi'
import { VersionTable } from '@/components/Table'

function Index({ params: { id } }) {
  const columns = [
    { title: '版本号', dataIndex: 'version' },
    // { title: '指导名称', dataIndex: 'guidanceName' },
    { title: '变更时间', dataIndex: 'updateTime', dateFormat: 'yyyy-MM-DD HH:mm:ss' },
    { title: '变更创建人', dataIndex: 'operatorName' },
  ]
  const { month } = getQuery()
  return (
    <Page header={null}>
      <VersionTable
        params={{ mainId: id, module: 'NEW_FTP_GUIDANCE' }}
        getListApi={newFtpBaseInfoApi.postInfoVersions}
        toDifferentInfo={(logId) => store.toDifferentInfo(logId, month)}
        columns={columns}
      />
    </Page>
  )
}

export default observer(Index)
