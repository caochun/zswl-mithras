import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import Api from '@/api/dashboard/plan'
import ExportBtn from '../../../../Export'
import TableSummary from '../../../../TableSummary'
import { useState } from 'react'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const [sumData, setSumData] = useState({})
  const { activityKey } = store
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, ['部门', '项目名称', '合同编号'])

  const table = Table.useStore(
    {
      request: async (params) => {
        const { records, sumData } = await Api.postDashboardPlanList({
          ...params,
          queryType: activityKey,
        })
        setSumData(sumData)
        return records
      },
    },
    [activityKey]
  )

  return (
    <Drawer
      store={store.investmentDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={'计划执行情况'}
      onClose={store.investmentDrawer.close}
    >
      <Table
        extra={
          <ExportBtn
            tableStore={table}
            businessType={'DASHBOARD_PROJECT_VIEW_PLAN_EXECUTE'}
            extraParams={{ queryType: activityKey }}
          />
        }
        editable={false}
        summary={() => {
          return (
            <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
          )
        }}
        columnsFilter={`项目视图_计划执行情况_公司合计`}
        onFilter={(key,val) => saveServer('项目视图_计划执行情况_公司合计',val)}
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
