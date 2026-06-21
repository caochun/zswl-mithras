import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import Api from '@/api/dashboard/customerOverview'
import { columnsFilterKey } from '../../Config'
import ExportBtn from '../../../../../Export'
import TableSummary from '../../../../../TableSummary'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 所有客户
const AllCustomerListTable = ({ group }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    '客户名称',
    '风控行业分类',
    '资产五级分类',
    '省份',
    '所属部门',
    '所属主办',
  ])

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postDashboardClientOverviewAllList(params)
      setSumData(sumData)
      return records
    },
  })

  return (
    <Table
      onFilter={(key, val) => saveServer(`${columnsFilterKey}_${group}`, val)}
      extra={<ExportBtn tableStore={table} businessType={'DASHBOARD_CLIENT_OVERVIEW_ALL'} />}
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      editable={false}
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

export default observer(AllCustomerListTable)
