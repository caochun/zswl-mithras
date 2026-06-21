import { observer } from '@zswl/admin'
import { Table, Button } from '@zswl/components'
import { getTableColumns, getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { columnsFilterKey } from '../../Config'
import { downLoadExcel } from '@/components/Excel'
import Api from '@/api/dashboard/afterLeaseCheck'
import { saveServer } from '@/utils'

// 租后检查
const Index = ({ group }) => {
  const columns = getTableColumns(ALL_COLUMNS)
  const searchItem = getSearchColumns(ALL_COLUMNS, ['客户名称', '计划状态', '本次检查形式'])

  const table = Table.useStore({
    request: (params) => {
      return Api.postDashboardAfterLeaseCheckList(params)
    },
  })

  const exportExcel = async () => {
    const params = table.getParams()
    const postParams = {
      ...params,
      pageSize: 5000,
      page: 1,
    }
    const res = await Api.postDashboardAfterLeaseCheckList(postParams)
    downLoadExcel({
      fileName: `租后管理详情`,
      dataSource: res,
      columns: [...columns],
    })
  }

  return (
    <Table
      extra={
        <Button type="primary" onClick={exportExcel}>
          导出
        </Button>
      }
      rowKey={'idKey'}
      editable={false}
      columnsFilter={`${columnsFilterKey}_${group}`}
      onFilter={(key, val) => saveServer(`${columnsFilterKey}_${group}`, val)}
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
