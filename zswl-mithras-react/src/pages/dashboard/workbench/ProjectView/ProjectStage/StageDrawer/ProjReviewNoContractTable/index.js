import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns, isLawDept } from '@/utils'
import { All_COLUMNS } from './Column'
import Api from '@/pages/dashboard/workbench/ProjectView/api'
import { columnsFilterKey } from '../../Config'
import { ExportBtn, TableSummary } from '@/pages/dashboard/workbench/components'
import { useState } from 'react'
import { saveServer } from '@/utils'

// 评审通过未创建合同
const Index = ({ group, extraQueryParams }) => {
  const [sumData, setSumData] = useState({})
  let names = [
    '客户名称',
    '项目名称',
    '当前阶段停留天数(工作日)',
    '风控行业分类',
    '授信金额(元)',
    '国标行业分类',
    '业务类型',
    '承租人',
    '担保人',
    '业务部门',
    '项目主办',
    '项目协办',
    '申请时间',
  ]
  let formNames = ['客户名称', '项目名称', '业务部门', '项目主办']
  if (isLawDept()) {
    names = [
      '客户名称',
      '项目名称',
      '申报授信金额(元)',
      '项目状态',
      '审批状态',
      '风控行业分类',
      '当前阶段停留天数(工作日)',
      '国标行业分类',
      '业务类型',
      '承租人',
      '担保人',
      '业务部门',
      '项目主办',
      '项目协办',
      '申请时间',
    ]
    formNames = ['客户名称', '项目名称', '合同编号', '业务部门', '项目主办']
  }

  const columns = getTableColumns(All_COLUMNS, names)
  const searchItem = getSearchColumns(All_COLUMNS, formNames)

  const table = Table.useStore({
    request: async (params) => {
      const { list, sumData } = await Api.postProjectStageProjreviewNocontractList({
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
          businessType={'DASHBOARD_PROJECT_STAGE_REVIEW_NO_CONTRACT'}
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

export default observer(Index)
