import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { financingViewColumnsFilterKey as columnsFilterKey } from '@/dashboard/DashboardUtilsFilterKeys'
import Api from '../../api'
import { ExportBtn, TableSummary } from '@/components/Dashboard'
import { useState, useEffect } from 'react'
import { saveServer } from '@/utils'

// 资金成本
const Index = ({ groupCode, curCardData }) => {
  console.log('curCardData44: ', curCardData)
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, ['融资编号', '融资机构/产品名称', '融资类别'])

  const table = Table.useStore({
    request: async (params) => {
      console.log('params14: ', params)
      const { records, sumData } = await Api.postDashboardFinanceFundsList(params)
      setSumData(sumData)
      return records
    },
  })
  useEffect(() => {
    const newParams = {
      financingTypeCode: curCardData?.type,
      isThisYear: curCardData?.isThisYear,
      isThisMonth: curCardData?.isThisMonth,
    }
    table.setParams(newParams)
    table.search(newParams)
  }, [])
  table.setParams({
    queryDate: curCardData?.queryDate,
  })
  return (
    <Table
      autoRequest={false}
      extra={
        <ExportBtn
          tableStore={table}
          businessType={'DASHBOARD_FUND_FINANCE_COST_FUNDS'}
          extraParams={{}}
        />
      }
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
