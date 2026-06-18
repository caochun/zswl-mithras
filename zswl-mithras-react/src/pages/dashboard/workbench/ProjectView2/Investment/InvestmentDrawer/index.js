import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import Api from '../api'
import { DashboardExportBtn as ExportBtn, DashboardTableSummary as TableSummary } from '@/components/Dashboard/DashboardEntries'
import { useState } from 'react'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const [sumData, setSumData] = useState({})
  const { activityKey, queryDimension } = store
  const isReceiptDimension = queryDimension === 'RECEIPT'
  const columns = getTableColumns(ALL_COLUMNS(isReceiptDimension))
  const searchItem = getSearchColumns(
    ALL_COLUMNS(),
    ['客户名称', '合同编号', '投放日期'].filter(Boolean)
  )

  const table = Table.useStore(
    {
      request: async (params) => {
        const { records, sumData } = await Api.postDashboardPayList({
          ...params,
          queryType: activityKey,
          queryDimension,
        })
        setSumData(sumData)
        return records
      },
    },
    [activityKey, queryDimension]
  )

  return (
    <Drawer
      store={store.investmentDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={'投放情况'}
      onClose={store.investmentDrawer.close}
    >
      <Table
        extra={
          <ExportBtn
            tableStore={table}
            businessType={'DASHBOARD_PROJECT_VIEW_INVEST_CASE'}
            extraParams={{ queryType: activityKey, queryDimension }}
          />
        }
        editable={false}
        summary={() => {
          return (
            <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
          )
        }}
        columnsFilter={`项目视图_投放情况_公司合计`}
        onFilter={(key,val) => saveServer('项目视图_投放情况_公司合计',val)}
        scroll={{ x: true }}
        store={table}
        searchbar={{
          items: searchItem,
        }}
        columns={[...columns]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
