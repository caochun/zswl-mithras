import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { financingViewColumnsFilterKey as columnsFilterKey } from '@/utils/domains/dashboard/DashboardUtilsFilterKeys'
import Api from '@/api/dashboard/finance'
import { DashboardExportBtn as ExportBtn, DashboardTableSummary as TableSummary } from '@/components/Dashboard/DashboardEntries'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 授信情况
const Index = ({ groupCode, curCardData }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, ['授信编号', '融资机构'])

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postDashboardFinanceCreditinfoList(params)
      setSumData(sumData)
      return records
    },
  })
  table.setParams({ queryDate: curCardData?.queryDate })

  return (
    <Table
      extra={<ExportBtn tableStore={table} businessType={'DASHBOARD_FUND_FINANCE_CREDIT'} />}
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      editable={false}
      columnsFilter={`${columnsFilterKey}_${groupCode}`}
      onFilter={(key, val) => saveServer(`${columnsFilterKey}_${groupCode}`, val)}
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
