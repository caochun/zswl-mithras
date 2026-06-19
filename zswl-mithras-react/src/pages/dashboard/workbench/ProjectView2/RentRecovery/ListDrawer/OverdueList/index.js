import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { rentRecoveryColumnsFilterKey as columnsFilterKey } from '@/utils/domains/dashboard/DashboardUtilsFilterKeys'
import Api from '@/api/dashboard/projectView'
import { DashboardExportBtn as ExportBtn, DashboardTableSummary as TableSummary } from '@/components/Dashboard/DashboardEntries'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 存在逾期项目明细
const Index = ({ group, extraQueryParams = {} }) => {
  const [sumData, setSumData] = useState({})

  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    '客户名称',
    '项目名称',
    '合同编号',
    '业务部门',
    '项目主办',
  ])

  const table = Table.useStore(
    {
      request: async (params) => {
        const { list, sumData } = await Api.postProjectInfoOverdueList({
          ...params,
          ...extraQueryParams,
        })
        setSumData(sumData)
        return list
      },
    },
    [extraQueryParams]
  )

  return (
    <Table
      extra={
        <ExportBtn
          tableStore={table}
          businessType={'DASHBOARD_PROJECT_OVERDUE_LIST'}
          extraParams={{}}
        />
      }
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      editable={false}
      columnsFilter={`${columnsFilterKey}_OverdueList_1`}
      onFilter={(key,val) => saveServer(`${columnsFilterKey}_${group}`,val)}
      
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
