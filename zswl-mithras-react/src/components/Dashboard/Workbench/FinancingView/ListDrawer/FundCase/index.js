import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { financingViewColumnsFilterKey as columnsFilterKey } from '@/utils/domains/dashboard/DashboardUtilsFilterKeys'
import Api from '@/api/dashboard/finance'
import ExportBtn from '../../../../Export'
import TableSummary from '../../../../TableSummary'
import { useState } from 'react'
import { saveServer } from '@/utils'
import { AmountColumn } from '@/components/Format'
const mapParams = {
  // 存量
  FUND_FINANCE_LOAN: { isThisYear: 0, isThisMonth: 0 },
  // 本年新增
  FUND_FINANCE_LOAN_THIS_YEAR: { isThisYear: 1, isThisMonth: 0 },
  // 本月新增
  FUND_FINANCE_LOAN_THIS_MONTH: { isThisYear: 1, isThisMonth: 1 },
  // 成本资金
  FOND_FINANCE_COST_FOUNDS: { isThisYear: 1, isThisMonth: 0 },
}

// 融资情况
const Index = ({ groupCode, curCardData }) => {
  const [sumData, setSumData] = useState({})
  const getTableColumns = (groupCode) => {
    const columns = [...ALL_COLUMNS]
    // 在融资编号后插入动态列
    const insertIndex = columns.findIndex((col) => col.dataIndex === 'financingCode') + 1

    if (curCardData?.amountType === 'amount') {
      // 插入融资金额列
      columns.splice(
        insertIndex,
        0,
        AmountColumn({
          title: '融资金额(元)',
          initFormat: 1,
          dataIndex: 'loanAmount',
          sorter: {
            compare: (a, b) => a.loanAmount - b.loanAmount,
          },
        })
      )
    } else {
      // 插入融资余额列
      columns.splice(
        insertIndex,
        0,
        AmountColumn({
          title: '融资余额(元)',
          initFormat: 1,
          dataIndex: 'balanceAmount',
          sorter: {
            compare: (a, b) => a.balanceAmount - b.balanceAmount,
          },
        })
      )
    }

    return columns
  }
  // const columns = ['FUND_FINANCE_LOAN_THIS_MONTH', 'FUND_FINANCE_LOAN_THIS_YEAR'].includes(
  //   curCardData?.groupCode
  // )
  //   ? getTableColumns(ALL_COLUMNS, curCardData?.groupCode)
  //   : getTableColumns(ALL_COLUMNS)
  const columns = getTableColumns(groupCode)
  const searchItem = getSearchColumns(ALL_COLUMNS, ['融资类别', '融资机构/产品名称'])

  const otherParams = mapParams[groupCode]

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postDashboardFinanceLoanIfoList({
        ...params,
        ...otherParams,
      })
      setSumData(sumData)
      return records
    },
  })
  table.setParams({ queryDate: curCardData?.queryDate })

  return (
    <Table
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      extra={
        <ExportBtn
          tableStore={table}
          businessType={'DASHBOARD_FUND_FINANCE_LOAN_INFO'}
          extraParams={{
            ...otherParams,
          }}
        />
      }
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
