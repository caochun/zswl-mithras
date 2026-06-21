import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { financingViewColumnsFilterKey as columnsFilterKey } from '@/utils/domains/dashboard/DashboardUtilsFilterKeys'
import Api from '@/api/dashboard/finance'
import ExportBtn from '../../../../Export'
import TableSummary from '../../../../TableSummary'
import { useState } from 'react'
import styles from './index.less'
import { saveServer } from '@/utils'

// 还本付息
const FinancingRepayInterestTable = ({ groupCode, curCardData }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(ALL_COLUMNS, [
    '融资编号',
    '融资机构/产品名称',
    // '核销状态',
    '贷款金额(万元)',
    '剩余贷款金额(万元)',
    '本月计划应还金额(元)',
    '本月应还本金(元)',
    '本月应还利息(元)',
    '本月应还日期',
    '本月已还金额(元)',
    '本月未还金额(元)',
    '本月支付日期',
  ])
  const searchItem = getSearchColumns(ALL_COLUMNS, ['融资编号', '融资机构/产品名称'])

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postDashboardFinanceRepayList(params)
      setSumData(sumData)
      return records
    },
  })
  table.setParams({ queryDate: curCardData?.queryDate })

  return (
    <Table
      rowKey={'receiptRepayCashFlowId'}
      extra={
        <ExportBtn
          tableStore={table}
          businessType={'DASHBOARD_FUND_FINANCE_REPAY'}
          extraParams={{}}
        />
      }
      editable={false}
      summary={() => {
        return (
          <TableSummary
            columns={table.getOptimizedColumns()}
            sumData={sumData}
            startIndex={0}
          ></TableSummary>
        )
      }}
      columnsFilter={`${columnsFilterKey}_RepayInterest_1`}
      onFilter={(key, val) => saveServer(`${columnsFilterKey}_RepayInterest_1`, val)}
      scroll={{ x: true }}
      store={table}
      searchbar={{
        items: searchItem,
      }}
      columns={[...columns]}
      expandable={{
        expandedRowRender: (record) => {
          return (
            <Table
              columnsFilter={`${columnsFilterKey}_RepayInterest_2`}
              onFilter={(key, val) => saveServer(`${columnsFilterKey}_RepayInterest_2`, val)}
              pagination={false}
              className={styles.myTable}
              dataSource={record.subListInfo}
              columns={getTableColumns(ALL_COLUMNS, [
                '关联项目名称',
                '关联合同编号',
                '租金回笼金额(元)',
                '租金回笼金额(元)',
                '账户性质',
              ])}
            ></Table>
          )
        },
      }}
    ></Table>
  )
}

export default observer(FinancingRepayInterestTable)
