import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { All_COLUMNS } from './Column'
import Api from '@/api/dashboard/projectView'
import { columnsFilterKey } from '../../Config'
import ExportBtn from '../../../../Export'
import TableSummary from '../../../../TableSummary'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 立项
const ProjectStageEstablishTable = ({ group, extraQueryParams }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(All_COLUMNS)
  const searchItem = getSearchColumns(All_COLUMNS, [
    '客户名称',
    '项目名称',
    '立项状态',
    '审批状态',
    '业务部门',
    '项目主办',
  ])

  const table = Table.useStore({
    request: async (params) => {
      const { list, sumData } = await Api.postProjectStageProjestablishList({
        ...params,
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
          extraParams={{
            ...extraQueryParams,
          }}
          businessType={'DASHBOARD_PROJECT_STAGE_ESTABLISH'}
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
        items: searchItem,
      }}
      columns={[...columns]}
    ></Table>
  )
}

export default observer(ProjectStageEstablishTable)
