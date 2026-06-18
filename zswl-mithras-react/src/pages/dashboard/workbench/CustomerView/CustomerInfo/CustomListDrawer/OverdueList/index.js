import { observer } from '@zswl/admin'
import { useState } from 'react'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import Api from '@/api/dashboard/customerOverview'
import { columnsFilterKey } from '../../Config'
import { DashboardExportBtn as ExportBtn, DashboardTableSummary as TableSummary } from '@/components/Dashboard/DashboardEntries'
import { saveServer } from '@/utils'

// 逾期客户
const Index = ({ group }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    '客户名称',
    '项目名称',
    {
      title: '合同编号',
      dataIndex: 'contractCode',
    },
    '所属部门',
    '所属主办',
  ])

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postDashboardClientOverviewOverdueList(params)
      setSumData(sumData)
      return records
    },
  })

  return (
    <Table
    onFilter={(key,val) => saveServer(`${columnsFilterKey}_${group}`,val)}
      extra={<ExportBtn tableStore={table} businessType={'DASHBOARD_CLIENT_OVERVIEW_OVERDUE'} />}
      editable={false}
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      columnsFilter={`${columnsFilterKey}_${group}`}
      scroll={{ x: true }}
      store={table}
      searchbar={{
        items: searchItem,
      }}
      columns={[...columns]}
    ></Table>
  )
}

export default observer(Index)
