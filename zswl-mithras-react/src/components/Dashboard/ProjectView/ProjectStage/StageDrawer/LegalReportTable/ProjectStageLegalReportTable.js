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

// 法律合规
const ProjectStageLegalReportTable = ({ group, extraQueryParams }) => {
  const [sumData, setSumData] = useState({})
  const columns = getTableColumns(All_COLUMNS)
  const searchItem = getSearchColumns(All_COLUMNS, [
    '客户名称',
    '项目名称',
    '评审状态',
    '审批状态',
    '业务部门',
    '项目主办',
  ])

  const table = Table.useStore({
    request: async (params) => {
      const { list, sumData } = await Api.postProjectStageProjreviewLegalreportList({
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
          businessType={'DASHBOARD_PROJECT_STAGE_REVIEW_NO_LEGAL_REPORT'}
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
        items: searchItem,
      }}
      columns={[...columns]}
    ></Table>
  )
}

export default observer(ProjectStageLegalReportTable)
