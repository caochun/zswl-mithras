import { getTableColumns } from '@/utils'
import { Page, Table, TableStore, Tabs } from '@zswl/components'
import ALl_COLUMNS from '@/components/BlackGray/Columns'
import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { ExportAction } from '@/components/RiskActions'
import manualOutboundFormApi from '@/api/blackList/manualOutboundFormApi'
import { saveServer } from '@/utils'

const ReportList = () => {
  const columns = useMemo(() => {
    const nameColumns = [
      '企业名称',
      { title: '所属机构', dataIndex: 'applyOrganization', search: false },
      '业务类型',
      // '黑灰标识',
      {
        title: '计划出库时间',
        rename: '实际出库日期',
        dataIndex: 'actualOutboundTime',
        search: false,
      },
      { title: '更新时间', dataIndex: 'updateTime' },
    ]
    return getTableColumns(ALl_COLUMNS, nameColumns, true)
  }, [])
  const store = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          const res = await manualOutboundFormApi.postOutboundList({ ...params, auditStatus: 4 })
          return res
        },
      }),
    []
  )
  return (
    <Table
    columnsFilter={'outbound_search_1'}
            onFilter={(key,val) => saveServer('outbound_search_1',val)}
    
      columns={columns}
      serial
      selectable
      store={store}
      actions={[
        <ExportAction api={manualOutboundFormApi.getRecordExport} store={store} key="export" />,
      ]}
    ></Table>
  )
}

const Index = ({ props: { sub }, path }) => {
  return (
    <Page>
      <ReportList path={path} />
    </Page>
  )
}

export default observer(Index)
