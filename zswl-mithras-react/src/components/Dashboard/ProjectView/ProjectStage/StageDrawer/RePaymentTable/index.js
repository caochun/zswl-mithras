import { observer } from '@zswl/admin'
import { Table, SearchBar } from '@zswl/components'
import AmountRange from '@/components/AmountRange'
import { getTableColumns, getSearchColumns } from '@/utils'
import { All_COLUMNS } from './Column'
import Api from '@/api/dashboard/projectView'
import { columnsFilterKey } from '../../Config'
import ExportBtn from '../../../../Export'
import TableSummary from '../../../../TableSummary'
import { useState } from 'react'
import { saveServer } from '@/utils'

const { Item } = SearchBar

// 还款
const Index = ({ group, extraQueryParams }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(All_COLUMNS)
  const searchItem = getSearchColumns(All_COLUMNS, ['合同编号', '项目名称'])

  const table = Table.useStore({
    request: async (params) => {
      const { totalRentBalance, ...rest } = params
      const [totalRentBalanceFrom, totalRentBalanceTo] = totalRentBalance || []
      const { list, sumData } = await Api.postProjectStageRepaymentList({
        ...rest,
        totalRentBalanceFrom: totalRentBalanceFrom ? totalRentBalanceFrom * 10000 : undefined,
        totalRentBalanceTo: totalRentBalanceTo ? totalRentBalanceTo * 10000 : undefined,
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
          businessType={'DASHBOARD_PROJECT_STAGE_PREPARE_REPAYMENT'}
          transformParams={(values) => {
            const { totalRentBalance, ...rest } = values
            const [totalRentBalanceFrom, totalRentBalanceTo] = totalRentBalance || []
            return {
              ...rest,
              totalRentBalanceFrom: totalRentBalanceFrom ? totalRentBalanceFrom * 10000 : undefined,
              totalRentBalanceTo: totalRentBalanceTo ? totalRentBalanceTo * 10000 : undefined,
            }
          }}
          extraParams={{
            ...extraQueryParams,
          }}
        />
      }
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      editable={false}
      columnsFilter={`${columnsFilterKey}_${group}`}
      onFilter={(key,val) => saveServer(`${columnsFilterKey}_${group}`,val)}
      scroll={{ x: true }}
      store={table}
      searchbar={{
        items: [
          ...searchItem,
          <Item name="totalRentBalance" label="剩余金额">
            <AmountRange></AmountRange>
          </Item>,
        ],
      }}
      columns={[...columns]}
    ></Table>
  )
}

export default observer(Index)
