import { getQuery, observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { All_COLUMNS } from './Column'
import Api from '@/pages/dashboard/workbench/ProjectView/api'
import { columnsFilterKey } from '../../Config'
import { ExportBtn, TableSummary } from '@/pages/dashboard/workbench/components'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 本月应收租金
const Index = ({ group, extraQueryParams }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(All_COLUMNS)
  const searchItem = getSearchColumns(All_COLUMNS, [
    '项目名称',
    '合同编号',
    '本期应收日期',
    '业务部门',
    '项目主办',
  ])

  const table = Table.useStore({
    request: async (params) => {
      const { list, sumData } = await Api.postProjectInfoRentthismonthList({
        ...params,
        ...extraQueryParams,
      })
      setSumData(sumData)
      return list
    },
  })

  return (
    <Table
      extra={
        <ExportBtn
          tableStore={table}
          businessType={'DASHBOARD_PROJECT_RENT_THIS_MONTH'}
          extraParams={{ ...extraQueryParams }}
        />
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
