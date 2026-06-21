import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import ExportBtn from '../../../../Export'
import TableSummary from '../../../../TableSummary'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const { activityKey, listTableStore: table } = store
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, ['客户名称', '合同编号', '投放日期'])

  return (
    <Drawer
      store={store.listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={'投放收益率情况'}
      onClose={store.listDrawer.close}
    >
      <Table
              columnsFilter={'ThrowIncomeRate_ListDrawer_1'}
              onFilter={(key,val) => saveServer('ThrowIncomeRate_ListDrawer_1',val)}
        extra={
          <ExportBtn
            tableStore={table}
            businessType={'DASHBOARD_PROJECT_VIEW_INVEST_CASE'}
            extraParams={{ queryType: activityKey, queryDimension: 'RECEIPT', queryType: 'YEAR' }}
          />
        }
        editable={false}
        summary={() => {
          return (
            <TableSummary
              columns={table.getOptimizedColumns()}
              sumData={store.sumData}
            ></TableSummary>
          )
        }}
        columnsFilter={`经营全景视图_本年租赁业务投放收益率_详情`}
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
