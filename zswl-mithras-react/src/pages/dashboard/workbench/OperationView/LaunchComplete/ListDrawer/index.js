import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { columnsFilterKey } from '@/pages/dashboard/workbench/OperationView/Config'
import { ExportBtn, TableSummary } from '@/components/Dashboard'
import { initQueryDate } from '@/utils/dashboardOperation'
import { COMMON_COLUMNS } from '@/pages/dashboard/workbench/OperationView/Column'
import { saveServer } from '@/utils'

const groupName = '投放完成情况详情'

const Index = ({ store }) => {
  const { listDrawer, queryDate, listDrawerTable: table, sumData, averageData } = store

  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(COMMON_COLUMNS, ['时间区间', '业务组类别', '业务部门'])

  return (
    <Drawer
      store={listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={groupName}
      onClose={listDrawer.close}
    >
      <Table
        extra={
          <ExportBtn
            tableStore={table}
            businessType={'DASHBOARD_OPERATION_PAY_PLAN_EXECUTE'}
            extraParams={{}}
          />
        }
        summary={() => {
          return (
            <>
              <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
              <TableSummary
                title={'合计平均'}
                columns={table.getOptimizedColumns()}
                sumData={averageData}
              ></TableSummary>
            </>
          )
        }}
        editable={false}
        columnsFilter={`${columnsFilterKey}_${groupName}`}
        onFilter={(key,val) => saveServer(`${columnsFilterKey}_${groupName}`,val)}
        scroll={{ x: true }}
        store={table}
        searchbar={{
          initialValues: {
            queryDate: initQueryDate,
            // type: store.activityKey,
          },
          items: searchItem,
        }}
        columns={[...columns]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
