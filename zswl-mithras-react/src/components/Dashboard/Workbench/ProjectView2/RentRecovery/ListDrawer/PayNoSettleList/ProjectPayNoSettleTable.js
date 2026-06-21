import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { rentRecoveryColumnsFilterKey as columnsFilterKey } from '@/utils/domains/dashboard/DashboardUtilsFilterKeys'
import Api from '@/api/dashboard/projectView'
import ExportBtn from '../../../../../Export'
import TableSummary from '../../../../../TableSummary'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 已投放未结清项目
const ProjectPayNoSettleTable = ({ group }) => {
  const [sumData, setSumData] = useState({})

  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    '项目名称',
    '合同编号',
    '客户名称',
    '业务部门',
    '项目主办',
    '地区分类',
  ])

  const table = Table.useStore({
    request: async (params) => {
      const { records, sumData } = await Api.postProjectInfoPaynosettleList(params)
      setSumData(sumData)
      return records
    },
  })

  return (
    <Table
      extra={
        <ExportBtn
          tableStore={table}
          businessType={'DASHBOARD_PROJECT_PROJECT_PAY_NO_SETTLE'}
          extraParams={{}}
        />
      }
      summary={() => {
        return <TableSummary columns={table.getOptimizedColumns()} sumData={sumData}></TableSummary>
      }}
      editable={false}
      columnsFilter={`${columnsFilterKey}_PayNoSettleList_1`}
      onFilter={(key, val) => saveServer(`${columnsFilterKey}_PayNoSettleList_1`, val)}

      scroll={{ x: true }}
      store={table}
      searchbar={{
        items: searchItem,
      }}
      columns={[...columns]}
    ></Table>
  )
}

export default observer(ProjectPayNoSettleTable)
