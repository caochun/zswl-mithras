import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { rentRecoveryColumnsFilterKey as columnsFilterKey } from '@/utils/dashboardFilterKeys'
import Api from '../../api'
import { ExportBtn, TableSummary } from '@/components/Dashboard'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 项目质押/监管情况
const Index = ({ group }) => {
  const [sumData, setSumData] = useState({})

  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, ['合同编号', '项目名称', '质押/监管情况','融资状态','融资编号'])

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postProjectInfoStatisticsPledgeList(params)
      setSumData(sumData)
      return records
    },
  })

  return (
    <Table
      extra={
        <ExportBtn tableStore={table} businessType={'DASHBOARD_PROJECT_PLEDGE'} extraParams={{}} />
      }
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      rowKey={(record, index) => index.toString()}
      editable={false}
      columnsFilter={`${columnsFilterKey}_${group}`}
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
