import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { useEffect } from 'react'
import { ALL_COLUMNS } from './Column'
import { operationViewColumnsFilterKey as columnsFilterKey } from '@/utils/domains/dashboard/DashboardUtilsFilterKeys'
import { COMMON_COLUMNS } from '../../../OperationViewColumns'
import ExportBtn from '../../../Export'
import TableSummary from '../../../TableSummary'
import { initQueryDate } from '@/utils/domains/dashboard/DashboardUtilsOperation'
import { useState } from 'react'
import Api from '@/api/dashboard/operationView/conversionRatesApi'
import { saveServer } from '@/utils'

const group = '转化率情况详情'

const Index = ({ store }) => {
  const { queryDate, listDrawer, averageData, sumData, listDrawerTable: table } = store

  const columns = ALL_COLUMNS
  const searchItem = getSearchColumns(COMMON_COLUMNS, ['时间区间', '业务组类别', '业务部门'])

  return (
    <Drawer
      store={listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={group}
      onClose={listDrawer.close}
    >
      <Table
        title={() => <div>单位：%</div>}
        scroll={{ x: true }}
        bordered
        extra={
          <ExportBtn
            tableStore={table}
            businessType={'DASHBOARD_OPERATION_CONVERSION_EXECUTE'}
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
        columnsFilter={`${columnsFilterKey}_${group}`}
        onFilter={(key,val) => saveServer(`${columnsFilterKey}_${group}`,val)}
        store={table}
        searchbar={{
          initialValues: {
            queryDate: initQueryDate,
          },
          items: searchItem,
        }}
        columns={[...columns]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
