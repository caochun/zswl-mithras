import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import Api from '@/api/dashboard/customerAfterLease'
import { columnsFilterKey } from '../../Config'
import ExportBtn from '../../../../../Export'
import { saveServer } from '@/utils'

// 租后检查
const Index = ({ group }) => {
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, [
    '客户名称',
    '计划类型',
    '本次检查形式',
    '本次租后截止时间',
    '客户主办',
    '计划状态',
    '审批状态',
  ])

  const table = Table.useStore({
    request: (params) => {
      return Api.postDashboardClientAfterleaseCheckList(params)
    },
  })

  return (
    <Table
      extra={
        <ExportBtn
          tableStore={table}
          businessType={'DASHBOARD_CLIENT_OVERVIEW_AFTER_LEASE'}
          extraParams={{}}
        />
      }
      rowKey={'clientId'}
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
